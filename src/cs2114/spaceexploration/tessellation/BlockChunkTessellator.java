package cs2114.spaceexploration.tessellation;

import cs2114.spaceexploration.universe.Chunk;

// -------------------------------------------------------------------------
/**
 * BlockChunkTessellator is a ChunkTessellator that generates a cube surface
 * from the scalar fields.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class BlockChunkTessellator
    implements ChunkTessellator
{
    private float[]                vertices;
    private float[]                uv;
    private float[]                normals;
    private float[]                colors;
    private int[]                  index;

    private final int              TOP_FACE       = 1, BOTTOM_FACE = 1 << 1,
        LEFT_FACE = 1 << 2, RIGHT_FACE = 1 << 3, FRONT_FACE = 1 << 4,
        BACK_FACE = 1 << 5;
    private final int              TOP_FACE_INDEX = 0, BOTTOM_FACE_INDEX = 1,
        LEFT_FACE_INDEX = 2, RIGHT_FACE_INDEX = 3, FRONT_FACE_INDEX = 4,
        BACK_FACE_INDEX = 5;

    /*
     * A constant which stores all the vertex offsets for each face Accessed by
     * [face index]
     */
    private static final float[][] FACE_VERTS     = {
        { -.5f, .5f, .5f, .5f, .5f, .5f, .5f, .5f, -.5f, -.5f, .5f, -.5f },
        { -.5f, -.5f, .5f, .5f, -.5f, .5f, .5f, -.5f, -.5f, -.5f, -.5f, -.5f },
        { -.5f, .5f, .5f, -.5f, -.5f, .5f, -.5f, -.5f, -.5f, -.5f, .5f, -.5f },
        { .5f, .5f, .5f, .5f, -.5f, .5f, .5f, -.5f, -.5f, .5f, .5f, -.5f },
        { -.5f, .5f, -.5f, -.5f, -.5f, -.5f, .5f, -.5f, -.5f, .5f, .5f, -.5f },
        { -.5f, .5f, .5f, -.5f, -.5f, .5f, .5f, -.5f, .5f, .5f, .5f, .5f } };
    private static final float[][] FACE_NORMALS   = { { 0, 1, 0 },
        { 0, -1, 0 }, { -1, 0, 0 }, { 1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
    private static final short[]   FACE_INDICES   = { 0, 1, 2, 0, 2, 3 };


    public void tessellateChunk(Chunk c, int lodLevel)
    {
        int faces = getNumberOfFaces(c);
        /* 3 values per vertex, 4 vertices per face */
        vertices = new float[faces * 3 * 4];
        normals = new float[faces * 3 * 4];
        /* 4 color components per vertex, 4 vertices per face */
        colors = new float[faces * 4 * 4];
        /* 2 triangles per face, 3 index values per triangle */
        index = new int[faces * 2 * 3];
        /* 2 UV coordinates per vertex, 4 vertices per face */
        uv = new float[faces * 4 * 2];

        int vertexIndex = 0;
        int uvIndex = 0;
        int indicesIndex = 0;
        int colorsIndex = 0;
        for (int x = 0; x < Chunk.SIZE; x++)
        {
            for (int y = 0; y < Chunk.SIZE; y++)
            {
                for (int z = 0; z < Chunk.SIZE; z++)
                {
                    float value = c.getDensity(x, y, z);
                    if (value < 0)
                    {
                        continue;
                    }
                    int face = getFaces(c, x, y, z);
                    float[] color =
                        { (float)Math.random(), (float)Math.random(),
                            (float)Math.random(), 1 };
                    if ((face & TOP_FACE) != 0)
                    {
                        addVertices(vertexIndex, TOP_FACE_INDEX, x, y, z);
                        addNormals(vertexIndex, TOP_FACE_INDEX);
                        addIndices(indicesIndex, vertexIndex);
                        addUVs(uvIndex, value, TOP_FACE);
                        addColor(colorsIndex, color);
                        vertexIndex += 12;
                        indicesIndex += 6;
                        uvIndex += 8;
                        colorsIndex += 16;
                    }
                    if ((face & BOTTOM_FACE) != 0)
                    {
                        addVertices(vertexIndex, BOTTOM_FACE_INDEX, x, y, z);
                        addNormals(vertexIndex, BOTTOM_FACE_INDEX);
                        addIndices(indicesIndex, vertexIndex);
                        addUVs(uvIndex, value, BOTTOM_FACE);
                        addColor(colorsIndex, color);
                        vertexIndex += 12;
                        indicesIndex += 6;
                        uvIndex += 8;
                        colorsIndex += 16;
                    }
                    if ((face & LEFT_FACE) != 0)
                    {
                        addVertices(vertexIndex, LEFT_FACE_INDEX, x, y, z);
                        addNormals(vertexIndex, LEFT_FACE_INDEX);
                        addIndices(indicesIndex, vertexIndex);
                        addUVs(uvIndex, value, LEFT_FACE);
                        addColor(colorsIndex, color);
                        vertexIndex += 12;
                        indicesIndex += 6;
                        uvIndex += 8;
                        colorsIndex += 16;
                    }
                    if ((face & RIGHT_FACE) != 0)
                    {
                        addVertices(vertexIndex, RIGHT_FACE_INDEX, x, y, z);
                        addNormals(vertexIndex, RIGHT_FACE_INDEX);
                        addIndices(indicesIndex, vertexIndex);
                        addUVs(uvIndex, value, RIGHT_FACE);
                        addColor(colorsIndex, color);
                        vertexIndex += 12;
                        indicesIndex += 6;
                        uvIndex += 8;
                        colorsIndex += 16;
                    }
                    if ((face & BACK_FACE) != 0)
                    {
                        addVertices(vertexIndex, BACK_FACE_INDEX, x, y, z);
                        addIndices(indicesIndex, vertexIndex);
                        addNormals(vertexIndex, BACK_FACE_INDEX);
                        addUVs(uvIndex, value, BACK_FACE);
                        addColor(colorsIndex, color);
                        vertexIndex += 12;
                        indicesIndex += 6;
                        uvIndex += 8;
                        colorsIndex += 16;
                    }
                    if ((face & FRONT_FACE) != 0)
                    {
                        addVertices(vertexIndex, FRONT_FACE_INDEX, x, y, z);
                        addNormals(vertexIndex, FRONT_FACE_INDEX);
                        addIndices(indicesIndex, vertexIndex);
                        addUVs(uvIndex, value, FRONT_FACE);
                        addColor(colorsIndex, color);
                        vertexIndex += 12;
                        indicesIndex += 6;
                        uvIndex += 8;
                        colorsIndex += 16;
                    }
                }
            }
        }
    }


    private void addVertices(int vertexIndex, int faceIndex, int x, int y, int z)
    {
        for (int i = 0; i < FACE_VERTS[faceIndex].length / 3; i++)
        {
            vertices[vertexIndex + i * 3] = x + FACE_VERTS[faceIndex][i * 3];
            vertices[vertexIndex + i * 3 + 1] =
                y + FACE_VERTS[faceIndex][i * 3 + 1];
            vertices[vertexIndex + i * 3 + 2] =
                z + FACE_VERTS[faceIndex][i * 3 + 2];
        }
    }


    private void addNormals(int vertexIndex, int faceIndex)
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < FACE_NORMALS[faceIndex].length; j++)
            {
                normals[vertexIndex + i * 3 + j] = FACE_NORMALS[faceIndex][j];
            }
        }
    }


    private void addIndices(int indicesIndex, int vertIndex)
    {
        for (int i = 0; i < FACE_INDICES.length; i++)
        {
            index[indicesIndex + i] = FACE_INDICES[i] + vertIndex / 3;
        }
    }


    private void addUVs(int uvIndex, float value, int face)
    {
        uv[uvIndex] = 0;
        uv[uvIndex + 1] = 0;
        uv[uvIndex + 2] = 1;
        uv[uvIndex + 3] = 0;
        uv[uvIndex + 4] = 1;
        uv[uvIndex + 5] = 1;
        uv[uvIndex + 6] = 0;
        uv[uvIndex + 7] = 1;
    }


    private void addColor(int colorsIndex, float[] color)
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < color.length; j++)
            {
                colors[colorsIndex + i * 4 + j] = color[j];
            }
        }
    }


    private int getNumberOfFaces(Chunk c)
    {
        int faces = 0;
        for (int x = 0; x < Chunk.SIZE; x++)
        {
            for (int y = 0; y < Chunk.SIZE; y++)
            {
                for (int z = 0; z < Chunk.SIZE; z++)
                {
                    if (c.getDensity(x, y, z) == 0)
                    {
                        continue;
                    }
                    if (x == 0 || c.getDensity(x - 1, y, z) == 0)
                    {
                        faces++;
                    }
                    if (x == Chunk.SIZE - 1 || c.getDensity(x + 1, y, z) == 0)
                    {
                        faces++;
                    }
                    if (y == 0 || c.getDensity(x, y - 1, z) == 0)
                    {
                        faces++;
                    }
                    if (y == Chunk.SIZE - 1 || c.getDensity(x, y + 1, z) == 0)
                    {
                        faces++;
                    }
                    if (z == 0 || c.getDensity(x, y, z - 1) == 0)
                    {
                        faces++;
                    }
                    if (z == Chunk.SIZE - 1 || c.getDensity(x, y, z + 1) == 0)
                    {
                        faces++;
                    }
                }
            }
        }
        return faces;
    }


    private int getFaces(Chunk c, int x, int y, int z)
    {
        int face = 0;
        /* Set the bits for each face that should be drawn */
        if (x == 0 || c.getDensity(x - 1, y, z) == 0)
        {
            face |= LEFT_FACE;
        }
        if (x == Chunk.SIZE - 1 || c.getDensity(x + 1, y, z) == 0)
        {
            face |= RIGHT_FACE;
        }
        if (y == 0 || c.getDensity(x, y - 1, z) == 0)
        {
            face |= BOTTOM_FACE;
        }
        if (y == Chunk.SIZE - 1 || c.getDensity(x, y + 1, z) == 0)
        {
            face |= TOP_FACE;
        }
        if (z == 0 || c.getDensity(x, y, z - 1) == 0)
        {
            face |= FRONT_FACE;
        }
        if (z == Chunk.SIZE - 1 || c.getDensity(x, y, z + 1) == 0)
        {
            face |= BACK_FACE;
        }
        return face;
    }


    public float[] getVertices()
    {
        return vertices;
    }


    public float[] getTextureCoords()
    {
        return uv;
    }


    public float[] getColors()
    {
        return colors;
    }


    public float[] getNormals()
    {
        return normals;
    }


    public int[] getIndices()
    {
        return index;
    }

}
