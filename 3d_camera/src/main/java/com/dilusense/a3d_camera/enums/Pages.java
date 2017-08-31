package com.dilusense.a3d_camera.enums;

/**
 * 人脸比对域值设置
 * 
 * @author KuangCH
 * @version 2016/2/26
 */
public enum Pages {

	CAMERA(0,"相机"), SCANNER(1,"3d扫描"), MEASURE(2,"测距");

	private final int value;
	private final String name;

	private Pages(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}
	
	public static int indexOfValue(int value){
		Pages[] values = Pages.values();
		for (int i = 0; i < values.length; i++) {
			if(values[i].getValue() == value){
				return i;
			}
		}
		return -1;
	}
}