/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.cache.definitions;

import lombok.Data;

import java.util.Map;

@Data
public class ObjectDefinition
{
	private int id;
	private short[] retextureToFind;
	private int anInt2069 = 16;
	private boolean impassable = false;
	private String name = "null";
	private int[] objectModels;
	private int[] objectTypes;
	private short[] recolorToFind;
	private int mapFunction = -1;
	private short[] textureToReplace;
	private int sizeX = 1;
	private int sizeY = 1;
	private int anInt2083 = 0;
	private int[] anIntArray2084;
	private int offsetX = 0;
	private boolean nonFlatShading = false;
	private int mapDoorFlag = -1;
	private int animationID = -1;
	private int varbitID = -1;
	private int ambient = 0;
	private int contrast = 0;
	private String[] actions = new String[5];
	private int interactType = 2;
	private int mapSceneID = -1;
	private short[] recolorToReplace;
	private boolean aBool2097 = true;
	private int modelSizeX = 128;
	private int modelSizeHeight = 128;
	private int modelSizeY = 128;
	private int objectID;
	private int offsetHeight = 0;
	private int offsetY = 0;
	private boolean aBool2104 = false;
	private int clipType = -1;
	private int itemSupport = -1;
	private int[] configChangeDest;
	private boolean isRotated = false;
	private int varpID = -1;
	private int anInt2110 = -1;
	private boolean aBool2111 = false;
	private int anInt2112 = 0;
	private int anInt2113 = 0;
	private boolean impenetrable = true;
	private Map<Integer, Object> params = null;
}
