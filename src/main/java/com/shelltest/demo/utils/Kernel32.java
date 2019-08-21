package com.shelltest.demo.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author 18671
 */
public interface Kernel32 extends Library {
	Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

    /**
     * 需符合SDK规范  首字母大写
     * @param hProcess
     * @return
     */
	long GetProcessId(Long hProcess);
}
