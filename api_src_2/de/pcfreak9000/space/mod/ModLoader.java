package de.pcfreak9000.space.mod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.resource.loadervpc.ResourceManager;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceexplorer.game.launch.Launcher;
import de.pcfreak9000.spaceexplorer.util.Private;

/**
 * loads mods.
 *
 * @author pcfreak9000
 *
 */
@Private
public class ModLoader {
    
    public static final String THIS_INSTANCE_ID = "this";
    
    private static class TmpHolder {
        private final File file;
        private final Class<?> modclass;
        
        private TmpHolder(final Class<?> clazz, final File file) {
            this.file = file;
            this.modclass = clazz;
        }
    }
    
    private static Comparator<TmpHolder> comp = (o1, o2) -> {
        final Mod m1 = o1.modclass.getAnnotation(Mod.class);
        final Mod m2 = o2.modclass.getAnnotation(Mod.class);
        final int vt = m1.id().compareToIgnoreCase(m2.id());
        if (vt != 0) {
            return vt;
        }
        final long[] a1 = m1.version();
        final long[] a2 = m2.version();
        for (int i = 0; i < Math.min(a1.length, a2.length); i++) {
            final long d = a1[i] - a2[i];
            if (d != 0) {
                return (int) Math.signum(d);
            }
        }
        return a1.length - a2.length;
    };
    
    private static final Pattern ZIP_JAR_PATTERN = Pattern.compile("(.+).(zip|jar)$");
    private static final Logger LOGGER = Logger.getLogger(ModLoader.class);
    
    private final List<TmpHolder> modClasses = new ArrayList<>();
    private final List<ModContainer> modList = new ArrayList<>();
    private final List<ModContainer> readOnlyModList = Collections.unmodifiableList(modList);
    
    private final EventBus MOD_LOADING_BUS = new EventBus();
    
    public void load(AdvancedFile modsfolder) {
        this.classLoadMods(modsfolder.toFile());
        this.instantiate();
        this.dispatchInstances();
        this.registerEvents();
        this.preInit();
        this.init();
        this.postInit();
        LOGGER.info("Mod loading finished with " + modList.size() + " mod(s) loaded");
    }
    
    public List<ModContainer> getMods() {
        return readOnlyModList;
    }
    
    public void stageModResources(ResourceManager resourceManager, int i) {
        for (TmpHolder th : this.modClasses) {
            resourceManager.stage(new AdvancedFile(th.file.getAbsolutePath()), i);
        }
    }
    
    private void preInit() {
        LOGGER.info("mod pre-initialization stage");
        MOD_LOADING_BUS.post(new Se2DModPreInitEvent());
    }
    
    private void init() {
        LOGGER.info("mod initialization stage");
        MOD_LOADING_BUS.post(new Se2DModInitEvent());
    }
    
    private void postInit() {
        LOGGER.info("mod post-initialization stage");
        MOD_LOADING_BUS.post(new Se2DModPostInitEvent());
    }
    
    private void instantiate() {
        LOGGER.info("Instantiating mods...");
        for (final TmpHolder th : this.modClasses) {
            final Class<?> modClass = th.modclass;
            Object instance = null;
            try {
                modClass.getConstructor().setAccessible(true);
                instance = modClass.newInstance();
            } catch (InstantiationException | NoSuchMethodException e) {
                LOGGER.error(
                        "Mod could not be instantiated. Make sure a nullary-constructor is available and your mod class is non-abstract etc: "
                                + modClass.getAnnotation(Mod.class).id());
                continue;
            } catch (IllegalAccessException | SecurityException e) {
                LOGGER.error("Illegal Access: " + modClass.getAnnotation(Mod.class).id());
                e.printStackTrace();
                continue;
            } catch (final LinkageError e) {
                LOGGER.error("Incompatible Mod: " + th.modclass);
                continue;
            }
            if (modClass.getAnnotation(Mod.class).id().equals(THIS_INSTANCE_ID)) {
                LOGGER.error("The String \"" + THIS_INSTANCE_ID + "\" can not be used as Mod-ID: " + modClass);
                continue;
            }
            final ModContainer container = new ModContainer(modClass, modClass.getAnnotation(Mod.class), instance);
            if (modList.contains(container)) {
                //TODO better
                LOGGER.info("Skipping already loaded mod: " + container.getMod().id() + " (version "
                        + Arrays.toString(container.getMod().version()) + ")");
                continue;
            } else {
                LOGGER.info("Instantiated mod: " + container);
                modList.add(container);
            }
            if (!contains(Launcher.VERSION, container.getMod().se2dversion())) {
                LOGGER.warn("The mod " + container + " may not be compatible with this Se2D-Version!");
            }
        }
    }
    
    private void registerEvents() {
        LOGGER.info("Registering container event handlers...");
        for (final ModContainer container : modList) {
            MOD_LOADING_BUS.register(container.getInstance());
        }
    }
    
    private void dispatchInstances() {
        LOGGER.info("Dispatching instances...");
        for (final ModContainer container : modList) {
            final Field[] fields = container.getModClass().getDeclaredFields();
            for (final Field f : fields) {
                f.setAccessible(true);
                if (f.isAnnotationPresent(Instance.class)) {
                    final Instance wanted = f.getAnnotation(Instance.class);
                    if (wanted.id().equals(container.getMod().id()) || wanted.id().equals(THIS_INSTANCE_ID)) {
                        try {
                            f.set(container.getInstance(), container.getInstance());
                        } catch (final IllegalArgumentException e) {
                            LOGGER.warn("Wrong arg @ " + container);
                        } catch (final IllegalAccessException e) {
                            LOGGER.warn("Illegal access @ " + container);
                        }
                    } else {
                        boolean found = false;
                        for (final ModContainer wantedContainer : modList) {
                            if (wantedContainer == container) {
                                continue;
                            }
                            if (wantedContainer.getMod().id().equals(wanted.id())) {
                                found = true;
                                if (wanted.requiredVersion().length > 0) {
                                    if (!Arrays.equals(wantedContainer.getMod().version(), wanted.requiredVersion())) {
                                        LOGGER.warn("The mod " + container + " requires the version "
                                                + Arrays.toString(wanted.requiredVersion()) + " from the mod "
                                                + wantedContainer);
                                        break;
                                    }
                                }
                                if (wantedContainer.getMod().accessible()) {
                                    try {
                                        f.set(container.getInstance(), wantedContainer.getInstance());
                                    } catch (final IllegalArgumentException e) {
                                        LOGGER.warn("Wrong arg @ " + container);
                                    } catch (final IllegalAccessException e) {
                                        LOGGER.warn("Illegal access @ " + container);
                                    }
                                } else {
                                    LOGGER.warn(wantedContainer + " is not accessible for the mod " + container);
                                }
                                break;
                            }
                        }
                        if (!found) {
                            LOGGER.warn("Could not find " + wanted.id());
                        }
                    }
                }
            }
        }
    }
    
    private void classLoadMods(final File moddir) {
        final List<File> candidates = new ArrayList<>();
        discover(candidates, moddir);
        load(candidates);
    }
    
    private void load(List<File> candidates) {
        final URL[] urlarray = new URL[candidates.size()];
        for (int i = 0; i < urlarray.length; i++) {
            try {
                urlarray[i] = candidates.get(i).toURI().toURL();
            } catch (final MalformedURLException e) {
                LOGGER.error("Could not create some URL: " + candidates.get(i));
                e.printStackTrace();
            }
        }
        final URLClassLoader classloader = new URLClassLoader(urlarray);
        for (int i = 0; i < candidates.size(); i++) {
            JarFile jarfile = null;
            try {
                jarfile = new JarFile(candidates.get(i));
                
                for (final JarEntry entry : Collections.list(jarfile.entries())) {
                    if (entry.getName().toLowerCase().endsWith(".class")) {
                        Class<?> clazz = null;
                        try {
                            clazz = classloader.loadClass(entry.getName().replace("/", ".").replace(".class", ""));
                        } catch (final ClassNotFoundException e) {
                            LOGGER.error("Could not load: " + entry.getName());
                            continue;
                        } catch (final LinkageError e) {
                            LOGGER.info("Unexpected behaviour of a class detected: "
                                    + entry.getName().replace("/", ".").replace(".class", ""));
                            continue;
                        }
                        if (clazz.isAnnotationPresent(Mod.class)) {
                            this.modClasses.add(new TmpHolder(clazz, candidates.get(i)));
                        }
                    }
                }
            } catch (final IOException e) {
                LOGGER.warn("Could not read mod container: " + candidates.get(i));
                continue;
            } finally {
                if (jarfile != null) {
                    try {
                        jarfile.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            classloader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.modClasses.sort(comp);
        LOGGER.info("Found " + modClasses.size() + " mod candidates!");
    }
    
    private void discover(final List<File> files, final File f) {
        if (f.isDirectory()) {
            final File[] innerFiles = f.listFiles();
            for (final File inner : innerFiles) {
                discover(files, inner);
            }
        } else {
            final Matcher matcher = ZIP_JAR_PATTERN.matcher(f.getName());
            if (matcher.matches()) {
                files.add(f);
            }
        }
    }
    
    private boolean contains(final Object o, final Object[] os) {
        for (final Object po : os) {
            if (po.equals(o)) {
                return true;
            }
        }
        return false;
    }
    
}