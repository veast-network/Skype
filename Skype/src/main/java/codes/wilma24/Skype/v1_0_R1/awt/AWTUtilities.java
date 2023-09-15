package codes.wilma24.Skype.v1_0_R1.awt;

import java.awt.Window;
import java.lang.reflect.Method;

public class AWTUtilities {

	public static void setWindowOpacity(Window arg0, float arg1) throws Exception {
		Class<?> clazz = Class.forName("com.sun.awt.AWTUtilities");
		for (Method m : clazz.getMethods()) {
			if (m.getName().contains("setWindowOpacity")) {
				m.invoke(null, arg0, arg1);
				break;
			}
		}
	}

}
