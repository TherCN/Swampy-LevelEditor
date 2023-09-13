package thercn.swampy.leveleditor.AppUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginLoader {
	
	URLClassLoader classLoader;
	Class<?> clazz;
	Object instance;
	String PluginEntry;

	Field pluginName;
	Field pluginPackageName;
	Field pluginVersion;
	Field pluginEntry;
	Field pluginMainClass;

	public static void main(String[] args) {
		try {
			PluginLoader plugin = new PluginLoader("/sdcard/test.jar");
			plugin.LoadPlugin();
			System.out.println(plugin.getPluginInfo());
			plugin.RunPlugin();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public PluginLoader(String pluginFile) throws Exception {
		classLoader = new URLClassLoader(new URL[] { new URL("file://" + pluginFile) });
	}

    public void LoadPlugin() throws Exception {
		clazz = Class.forName("MainEntry", true, classLoader);
		instance = clazz.newInstance();
		Method loaded = clazz.getMethod("onPluginLoad");
		loaded.invoke(instance);

		pluginName = clazz.getField("PluginName");
		pluginPackageName = clazz.getField("PackageName");
		pluginVersion = clazz.getField("PluginVersion");
		pluginEntry = clazz.getField("PluginEntry");
		pluginMainClass = clazz.getField("PluginMainClass");
		clazz = null;
		PluginEntry = pluginPackageName.get(instance) + "." + pluginEntry.get(instance);
	}
	
	public StringBuilder getPluginInfo() throws Exception {
		StringBuilder stringBuilder = 
			new StringBuilder("插件名:" + pluginName.get(instance) + "\n");
		stringBuilder.append("包名:" + pluginPackageName.get(instance) + "\n");
		stringBuilder.append("主类:" + pluginMainClass.get(instance) + "\n");
		stringBuilder.append("版本:" + pluginVersion.get(instance) + "\n");
		stringBuilder.append("入口:" + pluginEntry.get(instance) + "()\n");
		return stringBuilder;
	}

	public void RunPlugin() throws Exception {
		Method method = clazz.forName(pluginMainClass.get(instance).toString())
			.getMethod(pluginEntry.get(instance).toString());
		System.out.println("插件入口方法:" + method.toString());
		method.invoke(instance);
	}
}
