package thercn.swampy.leveleditor.ImageModify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import thercn.swampy.leveleditor.MainActivity;

public class SpriteCroper {
    File file;
	String[] imageList;
	DocumentBuilder builder;

	/*public static void main(String[] args) { 
		SpriteCroper spriteCroper = new SpriteCroper(new File("/storage/emulated/0/SLE/Objects/star.hs"));
		try {
			String[] sprites = spriteCroper.getSpritesForObject();
			String[] imagelist = spriteCroper.getImageListForSprite(sprites[0]);
			String[] imageFiles = spriteCroper.getImageFileForSprite(sprites[0]);
			String[] objectPNG = spriteCroper.getPNGForImageList(imagelist[0]);
			for (int i = 0; i < objectPNG.length; i++) {

				System.out.println(objectPNG[i]);

			}

		} catch (Exception e) {}

	}*/

	public SpriteCroper(File objectFile) {
		file = objectFile;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String[] getImageListForSprite(String spriteFile) throws Exception {

		// 读取XML文件并解析
		Document doc = builder.parse(new File("/sdcard/SLE" + spriteFile));
		// 获取所有的Animation节点
		NodeList animationNodes = doc.getElementsByTagName("Animation");
		// 创建一个字符串数组来存储atlas属性值
		String[] atlasValues = new String[animationNodes.getLength()];
		// 遍历所有Animation节点并获取其atlas属性值
		for (int a = 0; a < animationNodes.getLength(); a++) {
			Node animationNode = animationNodes.item(a);
			Element element = (Element) animationNode;
			String atlasValue = element.getAttribute("atlas");
			atlasValues[a] = atlasValue;

		}
		// 使用HashSet来过滤重复字符串
		Set<String> set = new HashSet<>();
		for (String str : atlasValues) {
			set.add(str);
		}

		// 将过滤后的字符串存回数组
		String[] result = new String[set.size()];
		set.toArray(result);
		return result;
	}

	public String[] getImageFileForSprite(String spriteFile) throws Exception {
		Document doc = builder.parse(new File("/sdcard/SLE" + spriteFile));
		// 获取所有的Image元素
		NodeList root = doc.getElementsByTagName("Frame");
		String[] FileList = new String[root.getLength()];
		for (int i = 0; i < root.getLength(); i++) {
			if (root.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element sprite = (Element) root.item(i);
				FileList[i] = sprite.getAttribute("name");
			}
		}
		return FileList;
	}

	public String[] getSpritesForObject() throws Exception {
		Document doc = builder.parse(file);
		// 获取所有的Image元素
		NodeList root = doc.getElementsByTagName("Sprite");
		String[] spriteList = new String[root.getLength()];
		for (int i = 0; i < root.getLength(); i++) {
			if (root.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element sprite = (Element) root.item(i);
				spriteList[i] = sprite.getAttribute("filename");
			}
		}
		return spriteList;
	}

	public String[] getPNGForImageList(String imageListFile) throws Exception {
		Document doc = builder.parse(new File("/sdcard/SLE" + imageListFile));
		Element root = doc.getDocumentElement();
		NodeList imageNodes = root.getChildNodes();
		int count = 0;
		for (int i = 0; i < imageNodes.getLength(); i++) {
			Node imageNode = imageNodes.item(i);
			if (imageNode.getNodeType() == Node.ELEMENT_NODE) {
				count++;
			}
		}
		String[] PNGFiles = new String[count];
		int index = 0;
		for (int i = 0; i < imageNodes.getLength(); i++) {       
			Node imageNode = imageNodes.item(i);
			if (imageNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) imageNode;
				PNGFiles[index++] = element.getAttribute("name");
			}       
		}
		return PNGFiles;
	}

	public void cropImageList(String imageListFile) throws Exception {
		Document doc = builder.parse(new File(MainActivity.APPDIR + imageListFile));
		Element root = doc.getDocumentElement();
		NodeList imageNodes = root.getChildNodes();
		Bitmap bigBitmap = BitmapFactory.decodeFile(MainActivity.APPDIR + root.getAttribute("file"));
		Bitmap smallImage = null;
		for (int i = 0; i < imageNodes.getLength(); i++) {		
			Node imageNode = imageNodes.item(i);
			if (imageNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) imageNode;
				String name = element.getAttribute("name");
				int x = Integer.parseInt(element.getAttribute("rect").split(" ")[0]);
				int y = Integer.parseInt(element.getAttribute("rect").split(" ")[1]);
				int width = Integer.parseInt(element.getAttribute("rect").split(" ")[2]);
				int heigth = Integer.parseInt(element.getAttribute("rect").split(" ")[3]);
				smallImage = Bitmap.createBitmap(bigBitmap, x, y, width, heigth);

				OutputStream outputStream = new FileOutputStream("/sdcard/SLE/ImageListCache/" + name);
				smallImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			}
		}

	}

	public Bitmap mergeBitmaps(String[] imagePaths) {	
		Bitmap[] bitmaps = new Bitmap[imagePaths.length];
		int width = 0, height = 0;
		// 加载第一个图片并获取其大小作为合并后图片的大小
		bitmaps[0] = BitmapFactory.decodeFile("/sdcard/SLE/ImageListCache/" + imagePaths[0]);
		width = bitmaps[0].getWidth();
		height = bitmaps[0].getHeight();
		// 创建一个新的Bitmap，用于合并所有图片
		Bitmap mergedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mergedBitmap);
		// 计算每个图片在合并后的图片中的位置，并绘制第一个图片
		Rect srcRect = new Rect(0, 0, bitmaps[0].getWidth(), bitmaps[0].getHeight());
		canvas.drawBitmap(bitmaps[0], srcRect, new Rect(0, 0, width, height), null);
		// 绘制后面的图片并覆盖第一个图片
		int x = 0;
		for (int i = 1; i < imagePaths.length; i++) {
			Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/SLE/ImageListCache/" + imagePaths[i]);
			srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			canvas.drawBitmap(bitmap, srcRect, new Rect(x, 0, x + bitmap.getWidth(), bitmap.getHeight()), null);
			x += bitmap.getWidth();
		}
		return mergedBitmap;
	}
	



}
