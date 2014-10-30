package com.wajawinc.spaceexploration.tessellation;

import android.graphics.Color;
import android.util.Log;
import com.wajawinc.spaceexploration.universe.Chunk;

// -------------------------------------------------------------------------
/**
 * An implementation of the Marching Cubes algorithm based off of Paul Bourke's
 * original C++ implementation. Generates a smooth surface from a scalar field.
 */
public class MarchingCubesChunkTessellator
    implements ChunkTessellator
{
    private static final int[]   EDGE_TABLE     = { 0x0, 0x109, 0x203, 0x30a,
        0x406, 0x50f, 0x605, 0x70c, 0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03,
        0xe09, 0xf00, 0x190, 0x99, 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
        0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90, 0x230, 0x339,
        0x33, 0x13a, 0x636, 0x73f, 0x435, 0x53c, 0xa3c, 0xb35, 0x83f, 0x936,
        0xe3a, 0xf33, 0xc39, 0xd30, 0x3a0, 0x2a9, 0x1a3, 0xaa, 0x7a6, 0x6af,
        0x5a5, 0x4ac, 0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0,
        0x460, 0x569, 0x663, 0x76a, 0x66, 0x16f, 0x265, 0x36c, 0xc6c, 0xd65,
        0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60, 0x5f0, 0x4f9, 0x7f3, 0x6fa,
        0x1f6, 0xff, 0x3f5, 0x2fc, 0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3,
        0xbf9, 0xaf0, 0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55, 0x15c,
        0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950, 0x7c0, 0x6c9,
        0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc, 0xfcc, 0xec5, 0xdcf, 0xcc6,
        0xbca, 0xac3, 0x9c9, 0x8c0, 0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf,
        0xec5, 0xfcc, 0xcc, 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0,
        0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c, 0x15c, 0x55,
        0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650, 0xaf0, 0xbf9, 0x8f3, 0x9fa,
        0xef6, 0xfff, 0xcf5, 0xdfc, 0x2fc, 0x3f5, 0xff, 0x1f6, 0x6fa, 0x7f3,
        0x4f9, 0x5f0, 0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c,
        0x36c, 0x265, 0x16f, 0x66, 0x76a, 0x663, 0x569, 0x460, 0xca0, 0xda9,
        0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac, 0x4ac, 0x5a5, 0x6af, 0x7a6,
        0xaa, 0x1a3, 0x2a9, 0x3a0, 0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f,
        0xb35, 0xa3c, 0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33, 0x339, 0x230,
        0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c, 0x69c, 0x795,
        0x49f, 0x596, 0x29a, 0x393, 0x99, 0x190, 0xf00, 0xe09, 0xd03, 0xc0a,
        0xb06, 0xa0f, 0x905, 0x80c, 0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203,
        0x109, 0x0                             };

    private static final int[][] TRIANGLE_TABLE = { {}, { 0, 8, 3 },
        { 0, 1, 9 }, { 1, 8, 3, 9, 8, 1 }, { 1, 2, 10 }, { 0, 8, 3, 1, 2, 10 },
        { 9, 2, 10, 0, 2, 9 }, { 2, 8, 3, 2, 10, 8, 10, 9, 8 }, { 3, 11, 2 },
        { 0, 11, 2, 8, 11, 0 }, { 1, 9, 0, 2, 3, 11 },
        { 1, 11, 2, 1, 9, 11, 9, 8, 11 }, { 3, 10, 1, 11, 10, 3 },
        { 0, 10, 1, 0, 8, 10, 8, 11, 10 }, { 3, 9, 0, 3, 11, 9, 11, 10, 9 },
        { 9, 8, 10, 10, 8, 11 }, { 4, 7, 8 }, { 4, 3, 0, 7, 3, 4 },
        { 0, 1, 9, 8, 4, 7 }, { 4, 1, 9, 4, 7, 1, 7, 3, 1 },
        { 1, 2, 10, 8, 4, 7 }, { 3, 4, 7, 3, 0, 4, 1, 2, 10 },
        { 9, 2, 10, 9, 0, 2, 8, 4, 7 },
        { 2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4 }, { 8, 4, 7, 3, 11, 2 },
        { 11, 4, 7, 11, 2, 4, 2, 0, 4 }, { 9, 0, 1, 8, 4, 7, 2, 3, 11 },
        { 4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1 },
        { 3, 10, 1, 3, 11, 10, 7, 8, 4 },
        { 1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4 },
        { 4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3 },
        { 4, 7, 11, 4, 11, 9, 9, 11, 10 }, { 9, 5, 4 }, { 9, 5, 4, 0, 8, 3 },
        { 0, 5, 4, 1, 5, 0 }, { 8, 5, 4, 8, 3, 5, 3, 1, 5 },
        { 1, 2, 10, 9, 5, 4 }, { 3, 0, 8, 1, 2, 10, 4, 9, 5 },
        { 5, 2, 10, 5, 4, 2, 4, 0, 2 },
        { 2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8 }, { 9, 5, 4, 2, 3, 11 },
        { 0, 11, 2, 0, 8, 11, 4, 9, 5 }, { 0, 5, 4, 0, 1, 5, 2, 3, 11 },
        { 2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5 },
        { 10, 3, 11, 10, 1, 3, 9, 5, 4 },
        { 4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10 },
        { 5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3 },
        { 5, 4, 8, 5, 8, 10, 10, 8, 11 }, { 9, 7, 8, 5, 7, 9 },
        { 9, 3, 0, 9, 5, 3, 5, 7, 3 }, { 0, 7, 8, 0, 1, 7, 1, 5, 7 },
        { 1, 5, 3, 3, 5, 7 }, { 9, 7, 8, 9, 5, 7, 10, 1, 2 },
        { 10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3 },
        { 8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2 },
        { 2, 10, 5, 2, 5, 3, 3, 5, 7 }, { 7, 9, 5, 7, 8, 9, 3, 11, 2 },
        { 9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11 },
        { 2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7 },
        { 11, 2, 1, 11, 1, 7, 7, 1, 5 },
        { 9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11 },
        { 5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0 },
        { 11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0 },
        { 11, 10, 5, 7, 11, 5 }, { 10, 6, 5 }, { 0, 8, 3, 5, 10, 6 },
        { 9, 0, 1, 5, 10, 6 }, { 1, 8, 3, 1, 9, 8, 5, 10, 6 },
        { 1, 6, 5, 2, 6, 1 }, { 1, 6, 5, 1, 2, 6, 3, 0, 8 },
        { 9, 6, 5, 9, 0, 6, 0, 2, 6 }, { 5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8 },
        { 2, 3, 11, 10, 6, 5 }, { 11, 0, 8, 11, 2, 0, 10, 6, 5 },
        { 0, 1, 9, 2, 3, 11, 5, 10, 6 },
        { 5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11 },
        { 6, 3, 11, 6, 5, 3, 5, 1, 3 },
        { 0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6 },
        { 3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9 },
        { 6, 5, 9, 6, 9, 11, 11, 9, 8 }, { 5, 10, 6, 4, 7, 8 },
        { 4, 3, 0, 4, 7, 3, 6, 5, 10 }, { 1, 9, 0, 5, 10, 6, 8, 4, 7 },
        { 10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4 }, { 6, 1, 2, 6, 5, 1, 4, 7, 8 },
        { 1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7 },
        { 8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6 },
        { 7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9 },
        { 3, 11, 2, 7, 8, 4, 10, 6, 5 },
        { 5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11 },
        { 0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6 },
        { 9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6 },
        { 8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6 },
        { 5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11 },
        { 0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7 },
        { 6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9 }, { 10, 4, 9, 6, 4, 10 },
        { 4, 10, 6, 4, 9, 10, 0, 8, 3 }, { 10, 0, 1, 10, 6, 0, 6, 4, 0 },
        { 8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10 }, { 1, 4, 9, 1, 2, 4, 2, 6, 4 },
        { 3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4 }, { 0, 2, 4, 4, 2, 6 },
        { 8, 3, 2, 8, 2, 4, 4, 2, 6 }, { 10, 4, 9, 10, 6, 4, 11, 2, 3 },
        { 0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6 },
        { 3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10 },
        { 6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1 },
        { 9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3 },
        { 8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1 },
        { 3, 11, 6, 3, 6, 0, 0, 6, 4 }, { 6, 4, 8, 11, 6, 8 },
        { 7, 10, 6, 7, 8, 10, 8, 9, 10 },
        { 0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10 },
        { 10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0 },
        { 10, 6, 7, 10, 7, 1, 1, 7, 3 },
        { 1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7 },
        { 2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9 },
        { 7, 8, 0, 7, 0, 6, 6, 0, 2 }, { 7, 3, 2, 6, 7, 2 },
        { 2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7 },
        { 2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7 },
        { 1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11 },
        { 11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1 },
        { 8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6 },
        { 0, 9, 1, 11, 6, 7 }, { 7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0 },
        { 7, 11, 6 }, { 7, 6, 11 }, { 3, 0, 8, 11, 7, 6 },
        { 0, 1, 9, 11, 7, 6 }, { 8, 1, 9, 8, 3, 1, 11, 7, 6 },
        { 10, 1, 2, 6, 11, 7 }, { 1, 2, 10, 3, 0, 8, 6, 11, 7 },
        { 2, 9, 0, 2, 10, 9, 6, 11, 7 },
        { 6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8 }, { 7, 2, 3, 6, 2, 7 },
        { 7, 0, 8, 7, 6, 0, 6, 2, 0 }, { 2, 7, 6, 2, 3, 7, 0, 1, 9 },
        { 1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6 },
        { 10, 7, 6, 10, 1, 7, 1, 3, 7 },
        { 10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8 },
        { 0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7 },
        { 7, 6, 10, 7, 10, 8, 8, 10, 9 }, { 6, 8, 4, 11, 8, 6 },
        { 3, 6, 11, 3, 0, 6, 0, 4, 6 }, { 8, 6, 11, 8, 4, 6, 9, 0, 1 },
        { 9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6 },
        { 6, 8, 4, 6, 11, 8, 2, 10, 1 },
        { 1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6 },
        { 4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9 },
        { 10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3 },
        { 8, 2, 3, 8, 4, 2, 4, 6, 2 }, { 0, 4, 2, 4, 6, 2 },
        { 1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8 }, { 1, 9, 4, 1, 4, 2, 2, 4, 6 },
        { 8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1 },
        { 10, 1, 0, 10, 0, 6, 6, 0, 4 },
        { 4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3 },
        { 10, 9, 4, 6, 10, 4 }, { 4, 9, 5, 7, 6, 11 },
        { 0, 8, 3, 4, 9, 5, 11, 7, 6 }, { 5, 0, 1, 5, 4, 0, 7, 6, 11 },
        { 11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5 },
        { 9, 5, 4, 10, 1, 2, 7, 6, 11 },
        { 6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5 },
        { 7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2 },
        { 3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6 },
        { 7, 2, 3, 7, 6, 2, 5, 4, 9 }, { 9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7 },
        { 3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0 },
        { 6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8 },
        { 9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7 },
        { 1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4 },
        { 4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10 },
        { 7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10 },
        { 6, 9, 5, 6, 11, 9, 11, 8, 9 },
        { 3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5 },
        { 0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11 },
        { 6, 11, 3, 6, 3, 5, 5, 3, 1 },
        { 1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6 },
        { 0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10 },
        { 11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5 },
        { 6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3 },
        { 5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2 }, { 9, 5, 6, 9, 6, 0, 0, 6, 2 },
        { 1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8 }, { 1, 5, 6, 2, 1, 6 },
        { 1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6 },
        { 10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0 }, { 0, 3, 8, 5, 6, 10 },
        { 10, 5, 6 }, { 11, 5, 10, 7, 5, 11 },
        { 11, 5, 10, 11, 7, 5, 8, 3, 0 }, { 5, 11, 7, 5, 10, 11, 1, 9, 0 },
        { 10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1 },
        { 11, 1, 2, 11, 7, 1, 7, 5, 1 },
        { 0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11 },
        { 9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7 },
        { 7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2 },
        { 2, 5, 10, 2, 3, 5, 3, 7, 5 },
        { 8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5 },
        { 9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2 },
        { 9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2 }, { 1, 3, 5, 3, 7, 5 },
        { 0, 8, 7, 0, 7, 1, 1, 7, 5 }, { 9, 0, 3, 9, 3, 5, 5, 3, 7 },
        { 9, 8, 7, 5, 9, 7 }, { 5, 8, 4, 5, 10, 8, 10, 11, 8 },
        { 5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0 },
        { 0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5 },
        { 10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4 },
        { 2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8 },
        { 0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11 },
        { 0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5 },
        { 9, 4, 5, 2, 11, 3 }, { 2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4 },
        { 5, 10, 2, 5, 2, 4, 4, 2, 0 },
        { 3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9 },
        { 5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2 }, { 8, 4, 5, 8, 5, 3, 3, 5, 1 },
        { 0, 4, 5, 1, 0, 5 }, { 8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5 },
        { 9, 4, 5 }, { 4, 11, 7, 4, 9, 11, 9, 10, 11 },
        { 0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11 },
        { 1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11 },
        { 3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4 },
        { 4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2 },
        { 9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3 },
        { 11, 7, 4, 11, 4, 2, 2, 4, 0 },
        { 11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4 },
        { 2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9 },
        { 9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7 },
        { 3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10 },
        { 1, 10, 2, 8, 7, 4 }, { 4, 9, 1, 4, 1, 7, 7, 1, 3 },
        { 4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1 }, { 4, 0, 3, 7, 4, 3 },
        { 4, 8, 7 }, { 9, 10, 8, 10, 11, 8 }, { 3, 0, 9, 3, 9, 11, 11, 9, 10 },
        { 0, 1, 10, 0, 10, 8, 8, 10, 11 }, { 3, 1, 10, 11, 3, 10 },
        { 1, 2, 11, 1, 11, 9, 9, 11, 8 },
        { 3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9 }, { 0, 2, 11, 8, 0, 11 },
        { 3, 2, 11 }, { 2, 3, 8, 2, 8, 10, 10, 8, 9 }, { 9, 10, 2, 0, 9, 2 },
        { 2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8 }, { 1, 10, 2 },
        { 1, 3, 8, 9, 1, 8 }, { 0, 9, 1 }, { 0, 3, 8 }, {} };

    private float[]              vertices;
    private float[]              colors;
    private float[]              normals;
    private int[]                index;

    private int                  numVertices;
    private int                  numIndices;
    
    private int lodLevel;
    private Chunk c;


    public MarchingCubesChunkTessellator()
    {
    }


    public void tessellateChunk(Chunk c, int lodLevel)
    {
    	this.lodLevel = lodLevel;
    	this.c = c;
        calculateNumberOfVerticesAndIndices();
        if (numVertices == 0 || numIndices == 0)
            return;
        Log.d("Tessellator", "Verts: " + numVertices + " Index: " + numIndices);
        index = new int[numIndices];
        vertices = new float[numVertices * 3];
        colors = new float[numVertices * 4];
        normals = new float[numVertices * 3];
        int verticesIndex = 0;
        int indicesIndex = 0;
        int colorsIndex = 0;
        int normalsIndex = 0;
        float vertexTemp[][] = new float[12][3];
        float normalTemp[][] = new float[12][4];
        float colorTemp[][] = new float[12][4];
        for (int x = 0; x < Chunk.SIZE; x+=lodLevel)
        {
            for (int y = 0; y < Chunk.SIZE; y+=lodLevel)
            {
                for (int z = 0; z < Chunk.SIZE; z+=lodLevel)
                {
                    int cubeIndex = getCubeIndex(x, y, z);
                    int edgeTableValue = EDGE_TABLE[cubeIndex];
                    if ((edgeTableValue & 1) > 0)
                    {
                        vertexInterpolate(
                            x,
                            y,
                            z,
                            c.getDensity(x, y, z),
                            x + lodLevel,
                            y,
                            z,
                            c.getDensity(x + lodLevel, y, z),
                            vertexTemp[0]);
                        colorInterpolate(
                            c.getTemperature(x, y, z),
                            c.getTemperature(x + lodLevel, y, z),
                            colorTemp[0]);
                    }
                    if ((edgeTableValue & (1 << 1)) > 0)
                    {
                        vertexInterpolate(
                            x + lodLevel,
                            y,
                            z,
                            c.getDensity(x + lodLevel, y, z),
                            x + lodLevel,
                            y,
                            z + lodLevel,
                            c.getDensity(x + lodLevel, y, z + lodLevel),
                            vertexTemp[1]);
                        colorInterpolate(
                            c.getTemperature(x + lodLevel, y, z),
                            c.getTemperature(x + lodLevel, y, z + lodLevel),
                            colorTemp[1]);
                    }
                    if ((edgeTableValue & (1 << 2)) > 0)
                    {
                        vertexInterpolate(
                            x + lodLevel,
                            y,
                            z + lodLevel,
                            c.getDensity(x + lodLevel, y, z + lodLevel),
                            x,
                            y,
                            z + lodLevel,
                            c.getDensity(x, y, z + lodLevel),
                            vertexTemp[2]);
                        colorInterpolate(
                            c.getTemperature(x + lodLevel, y, z + lodLevel),
                            c.getTemperature(x, y, z + lodLevel),
                            colorTemp[2]);
                    }
                    if ((edgeTableValue & (1 << 3)) > 0)
                    {
                        vertexInterpolate(
                            x,
                            y,
                            z + lodLevel,
                            c.getDensity(x, y, z + lodLevel),
                            x,
                            y,
                            z,
                            c.getDensity(x, y, z),
                            vertexTemp[3]);
                        colorInterpolate(
                            c.getTemperature(x, y, z + lodLevel),
                            c.getTemperature(x, y, z),
                            colorTemp[3]);
                    }
                    if ((edgeTableValue & (1 << 4)) > 0)
                    {
                        vertexInterpolate(
                            x,
                            y + lodLevel,
                            z,
                            c.getDensity(x, y + lodLevel, z),
                            x + lodLevel,
                            y + lodLevel,
                            z,
                            c.getDensity(x + lodLevel, y + lodLevel, z),
                            vertexTemp[4]);
                        colorInterpolate(
                            c.getTemperature(x, y + lodLevel, z),
                            c.getTemperature(x + lodLevel, y + lodLevel, z),
                            colorTemp[4]);
                    }
                    if ((edgeTableValue & (1 << 5)) > 0)
                    {
                        vertexInterpolate(
                            x + lodLevel,
                            y + lodLevel,
                            z,
                            c.getDensity(x + lodLevel, y + lodLevel, z),
                            x + lodLevel,
                            y + lodLevel,
                            z + lodLevel,
                            c.getDensity(x + lodLevel, y + lodLevel, z + lodLevel),
                            vertexTemp[5]);
                        colorInterpolate(
                            c.getTemperature(x + lodLevel, y + lodLevel, z),
                            c.getTemperature(x + lodLevel, y + lodLevel, z + lodLevel),
                            colorTemp[5]);
                    }
                    if ((edgeTableValue & (1 << 6)) > 0)
                    {
                        vertexInterpolate(
                            x + 1,
                            y + 1,
                            z + 1,
                            c.getDensity(x + lodLevel, y + lodLevel, z + lodLevel),
                            x,
                            y + lodLevel,
                            z + lodLevel,
                            c.getDensity(x, y + lodLevel, z + lodLevel),
                            vertexTemp[6]);
                        colorInterpolate(
                            c.getTemperature(x + lodLevel, y + lodLevel, z + lodLevel),
                            c.getTemperature(x, y + lodLevel, z + lodLevel),
                            colorTemp[6]);
                    }
                    if ((edgeTableValue & (1 << 7)) > 0)
                    {
                        vertexInterpolate(
                            x,
                            y + lodLevel,
                            z + lodLevel,
                            c.getDensity(x, y + lodLevel, z + lodLevel),
                            x,
                            y + lodLevel,
                            z,
                            c.getDensity(x, y + lodLevel, z),
                            vertexTemp[7]);
                        colorInterpolate(
                            c.getTemperature(x, y + lodLevel, z + lodLevel),
                            c.getTemperature(x, y + lodLevel, z),
                            colorTemp[7]);
                    }
                    if ((edgeTableValue & (1 << 8)) > 0)
                    {
                        vertexInterpolate(
                            x,
                            y,
                            z,
                            c.getDensity(x, y, z),
                            x,
                            y + lodLevel,
                            z,
                            c.getDensity(x, y + lodLevel, z),
                            vertexTemp[8]);
                        colorInterpolate(
                            c.getTemperature(x, y, z),
                            c.getTemperature(x, y + lodLevel, z),
                            colorTemp[8]);
                    }
                    if ((edgeTableValue & (1 << 9)) > 0)
                    {
                        vertexInterpolate(
                            x + lodLevel,
                            y,
                            z,
                            c.getDensity(x + lodLevel, y, z),
                            x + lodLevel,
                            y + lodLevel,
                            z,
                            c.getDensity(x + lodLevel, y + lodLevel, z),
                            vertexTemp[9]);
                        colorInterpolate(
                            c.getTemperature(x + lodLevel, y, z),
                            c.getTemperature(x + lodLevel, y + lodLevel, z),
                            colorTemp[9]);
                    }
                    if ((edgeTableValue & (1 << 10)) > 0)
                    {
                        vertexInterpolate(
                            x + lodLevel,
                            y,
                            z + lodLevel,
                            c.getDensity(x + lodLevel, y, z + lodLevel),
                            x + lodLevel,
                            y + lodLevel,
                            z + lodLevel,
                            c.getDensity(x + lodLevel, y + lodLevel, z + lodLevel),
                            vertexTemp[10]);
                        colorInterpolate(
                            c.getTemperature(x + lodLevel, y, z + lodLevel),
                            c.getTemperature(x + lodLevel, y + lodLevel, z + lodLevel),
                            colorTemp[10]);
                    }
                    if ((edgeTableValue & (1 << 11)) > 0)
                    {
                        vertexInterpolate(
                            x,
                            y,
                            z + lodLevel,
                            c.getDensity(x, y, z + lodLevel),
                            x,
                            y + lodLevel,
                            z + lodLevel,
                            c.getDensity(x, y + lodLevel, z + lodLevel),
                            vertexTemp[11]);
                        colorInterpolate(
                            c.getTemperature(x, y, z + lodLevel),
                            c.getTemperature(x, y + lodLevel, z + lodLevel),
                            colorTemp[11]);
                    }
                    for (int i = 0; i < 12; i++)
                    {
                        if ((edgeTableValue & (1 << i)) == 0)
                        {
                            continue;
                        }
                        for (int j = 0; j < 3; j++)
                        {
                            vertices[verticesIndex++] = vertexTemp[i][j];
                            colors[colorsIndex++] = colorTemp[i][j];
                        }
                        colors[colorsIndex++] = colorTemp[i][3];
                        for (int j = 0; j < 4; j++)
                        {
                            normalTemp[i][j] = 0;
                        }
                    }
                    int totalVertices = verticesIndex / 3;
                    for (int i = 0; i < TRIANGLE_TABLE[cubeIndex].length; i++)
                    {
                        int tableIndex = TRIANGLE_TABLE[cubeIndex][i];
                        /* Calculate index of vertex */
                        int vertexIndex = totalVertices - 1;
                        for (int j = tableIndex + 1; j < 12; j++)
                        {
                            if ((edgeTableValue & (1 << j)) > 0)
                            {
                                vertexIndex--;
                            }
                        }
                        index[indicesIndex++] = vertexIndex;
                        if (i % 3 == 0)
                        {

                            int nextTableIndex =
                                TRIANGLE_TABLE[cubeIndex][i + 1];
                            int thirdTableIndex =
                                TRIANGLE_TABLE[cubeIndex][i + 2];
                            float vX =
                                vertexTemp[nextTableIndex][0]
                                    - vertexTemp[tableIndex][0];
                            float vY =
                                vertexTemp[nextTableIndex][1]
                                    - vertexTemp[tableIndex][1];
                            float vZ =
                                vertexTemp[nextTableIndex][2]
                                    - vertexTemp[tableIndex][2];
                            float wX =
                                vertexTemp[thirdTableIndex][0]
                                    - vertexTemp[tableIndex][0];
                            float wY =
                                vertexTemp[thirdTableIndex][1]
                                    - vertexTemp[tableIndex][1];
                            float wZ =
                                vertexTemp[thirdTableIndex][2]
                                    - vertexTemp[tableIndex][2];
                            float normX = vY * wZ - vZ * wY;
                            float normY = vZ * wX - vX * wZ;
                            float normZ = vX * wY - vY * wX;
                            float length =
                                (float)Math.sqrt(normX * normX + normY * normY
                                    + normZ * normZ);
                            normX /= length;
                            normY /= length;
                            normZ /= length;
                            normalTemp[tableIndex][0] += normX;
                            normalTemp[tableIndex][1] += normY;
                            normalTemp[tableIndex][2] += normZ;
                            normalTemp[tableIndex][3]++;
                            normalTemp[nextTableIndex][0] += normX;
                            normalTemp[nextTableIndex][1] += normY;
                            normalTemp[nextTableIndex][2] += normZ;
                            normalTemp[nextTableIndex][3]++;
                            normalTemp[thirdTableIndex][0] += normX;
                            normalTemp[thirdTableIndex][1] += normY;
                            normalTemp[thirdTableIndex][2] += normZ;
                            normalTemp[thirdTableIndex][3]++;
                        }
                    }
                    for (int i = 0; i < 12; i++)
                    {
                        if ((edgeTableValue & (1 << i)) > 0)
                        {
                            for (int j = 0; j < 3; j++)
                            {
                                normals[normalsIndex++] = normalTemp[i][j];
                            }
                        }
                    }
                }
            }
        }
    }


    private static void vertexInterpolate(
        int x1,
        int y1,
        int z1,
        float value1,
        int x2,
        int y2,
        int z2,
        float value2,
        float[] vert)
    {
        if (abs(value1) < 0.0001f)
        {
            vert[0] = x1;
            vert[1] = y1;
            vert[2] = z1;
            return;
        }
        if (abs(value2) < 0.0001f)
        {
            vert[0] = x2;
            vert[1] = y2;
            vert[2] = z2;
            return;
        }
        if (abs(value1 - value2) < 0.0001f)
        {
            vert[0] = x1;
            vert[1] = y1;
            vert[2] = z1;
            return;
        }
        float interp = (0 - value1) / (value2 - value1);
        vert[0] = x1 + interp * (x2 - x1);
        vert[1] = y1 + interp * (y2 - y1);
        vert[2] = z1 + interp * (z2 - z1);
    }


    private static void setColor(float hue, float sat, float val, float[] color)
    {
        int argb = Color.HSVToColor(new float[] { hue, sat, val });
        color[0] = Color.red(argb) / 255.0f;
        color[1] = Color.green(argb) / 255.0f;
        color[2] = Color.blue(argb) / 255.0f;
        color[3] = 1;
    }


    private static void colorInterpolate(
        float value1,
        float value2,
        float[] color)
    {
        float hue1 = 0.0f;
        float sat1 = 1f;
        float val1 = 1;
        float hue2 = 360.0f;
        float sat2 = 1f;
        float val2 = 1;
        if (abs(value1) < 0.0001f)
        {
            setColor(hue1, sat1, val1, color);
            return;
        }
        if (abs(value2) < 0.0001f)
        {
            setColor(hue2, sat2, val2, color);
            return;
        }
        if (abs(value1 - value2) < 0.0001f)
        {
            setColor(hue1, sat1, val1, color);
            return;
        }
        float interp = (0 - value1) / (value2 - value1);
        interp = (float) Math.cos(interp*Math.PI*2)*.5f + .5f;
        Log.d("interp", interp+"");
        setColor(
            hue1 + interp * (hue2 - hue1),
            sat1 + interp * (sat2 - sat1),
            val1 + interp * (val2 - val1),
            color);
    }


    private static float abs(float x)
    {
        return x < 0 ? -x : x;
    }


    private void calculateNumberOfVerticesAndIndices()
    {
        numVertices = 0;
        numIndices = 0;
        for (int x = 0; x < Chunk.SIZE; x+=lodLevel)
        {
            for (int y = 0; y < Chunk.SIZE; y+=lodLevel)
            {
                for (int z = 0; z < Chunk.SIZE; z+=lodLevel)
                {
                    int cubeIndex = getCubeIndex(x, y, z);
                    numIndices += TRIANGLE_TABLE[cubeIndex].length;
                    int edgeTableValue = EDGE_TABLE[cubeIndex];
                    for (int i = 0; i < 12; i++)
                    {
                        if ((edgeTableValue & (1 << i)) > 0)
                        {
                            numVertices++;
                        }
                    }
                }
            }
        }
    }


    private int getCubeIndex(int x, int y, int z)
    {
        int cubeIndex = 0;
        if (c.getDensity(x, y, z) <= 0)
        {
            cubeIndex |= 1;
        }
        if (c.getDensity(x + lodLevel, y, z) <= 0)
        {
            cubeIndex |= 1 << 1;
        }
        if (c.getDensity(x + lodLevel, y, z + lodLevel) <= 0)
        {
            cubeIndex |= 1 << 2;
        }
        if (c.getDensity(x, y, z + lodLevel) <= 0)
        {
            cubeIndex |= 1 << 3;
        }
        if (c.getDensity(x, y + lodLevel, z) <= 0)
        {
            cubeIndex |= 1 << 4;
        }
        if (c.getDensity(x + lodLevel, y + lodLevel, z) <= 0)
        {
            cubeIndex |= 1 << 5;
        }
        if (c.getDensity(x + lodLevel, y + lodLevel, z + lodLevel) <= 0)
        {
            cubeIndex |= 1 << 6;
        }
        if (c.getDensity(x, y + lodLevel, z + lodLevel) <= 0)
        {
            cubeIndex |= 1 << 7;
        }
        return cubeIndex;
    }


    public float[] getVertices()
    {
        return vertices;
    }


    public float[] getTextureCoords()
    {
        return null;
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
