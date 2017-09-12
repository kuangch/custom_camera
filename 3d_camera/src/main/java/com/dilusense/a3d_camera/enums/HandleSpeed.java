package com.dilusense.a3d_camera.enums;

/**
 * 帧率处理速度枚举类（单位：帧）
 * 
 * @author KuangCH
 * @version 2015/8/19
 */
public enum HandleSpeed {

	HEIGH(3), MEDIUM(5), LOW(10);

	private final int value;

	private HandleSpeed(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}