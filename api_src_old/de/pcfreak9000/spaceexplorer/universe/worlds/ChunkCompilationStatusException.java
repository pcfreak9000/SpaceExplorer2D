package de.pcfreak9000.spaceexplorer.universe.worlds;

import de.pcfreak9000.spaceexplorer.util.Private;

/**
 * Thrown in the {@link Chunk}
 *
 * @author pcfreak9000
 *
 */
@Private
public class ChunkCompilationStatusException extends RuntimeException {

    /**
     * whatever
     */
    private static final long serialVersionUID = 5392148752195289831L;

    public ChunkCompilationStatusException(final String msg) {
        super(msg);
    }

}
