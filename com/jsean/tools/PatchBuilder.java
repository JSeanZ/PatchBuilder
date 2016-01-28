package com.jsean.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: svn补丁增量生成工具
 * @author 27900
 * @date 2016-1-28 下午12:12:00
 */
public class PatchBuilder {
	/**
	 * 目标文件夹路径
	 */
	private final String TARGET_PATH = "D:/work/target/";
	/**
	 * 源文件文件夹路径
	 */
	private final String TOMCAT_PATH = "D:/tools/tomcat-6.0.37/webapps/";
	/**
	 * 待打包文件路径信息（来自svn的log）
	 */
	private final String CONFIG_PATH = "D:/work/target/config.txt";
	/**
	 * 项目名
	 */
	private final String APP_NAME = "dahuaCA";
	/**
	 * eclipse中源文件输出路径
	 */
	private final String srcPath = "/src/main/java/";
	/**
	 * eclipse中资源文件输出路径
	 */
	private final String resourcesPath = "/src/main/resources/";
	/**
	 * eclipse中web前端文件输出路径
	 */
	private final String webappPath = "/src/main/webapp/";

	
	/**
	 * 
	 * @Description: 生成补丁入口函数     
	 * @author 27900
	 * @date 2016-1-28 下午12:15:24
	 */
	public static void patchBuild(){
		PatchBuilder patchBuilder = new PatchBuilder();
		long startTime = System.currentTimeMillis();
		patchBuilder.buildPatch();
		long endTime = System.currentTimeMillis();
		System.err.println("build sussess! 耗时：" + (endTime - startTime) + "ms");
	}
	

	/**
	 * 
	 * @author JSean
	 * @Description 生成补丁文件
	 */
	@SuppressWarnings("resource")
	public void buildPatch() {
		File config = new File(CONFIG_PATH);
		
		String linestrString = "";
		try {
			if (config.exists()) {
				BufferedReader bufferedReader;
//				bufferedReader = new BufferedReader(new FileReader(config));
				FileInputStream fis=new FileInputStream(config);
				InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
				bufferedReader = new BufferedReader(isr);
				while ((linestrString = bufferedReader.readLine()) != null) {
					copyFileToTarget(linestrString);
				}
			} else {
				System.out.println(CONFIG_PATH + " 文件夹不存在！");
				return;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @Description: 拷贝源文件到目标文件
	 * @param filePath   config中的文件路径  
	 * @author 27900
	 * @date 2016-1-28 下午12:12:59
	 */
	public void copyFileToTarget(String filePath){
		if (filePath == null || "".equals(filePath))
			return;
		//源文件路径
		String sourceFile = "";
		//目标文件路径
		String targetFile = "";

		System.out.println("************************************");
		sourceFile=buildSourceFilePath(filePath);
		targetFile=buildTargetFilePath(filePath);
		System.out.println("目标文件路径： " + targetFile);
		System.out.println("源路径： " + sourceFile);
		
		if(targetFile==null || sourceFile==null){
			return;
		}
		if(copyFile(sourceFile, targetFile)){
			System.out.println("拷贝成功！");
		}else {
			System.out.println("拷贝失败！");
		}
		System.out.println("************************************");
		
	}
	/**
	 * 
	 * @Description: [获取文件在tomcat路径下的包名]
	 * @param filePath
	 * @return     
	 * @author 27900
	 * @date 2016-1-28 上午11:11:24
	 */
	public String getPackageFilePath(String filePath){
		String packagePath=null;
		//src文件
		if (filePath.contains(srcPath)) {
			packagePath = filePath.substring(filePath.indexOf(srcPath)
					+ srcPath.length(), filePath.length());
		//资源文件
		} else if (filePath.contains(resourcesPath)) {
			packagePath = filePath.substring(filePath.indexOf(resourcesPath)
					+ resourcesPath.length(), filePath.length());
		//web前端文件
		}else if(filePath.contains(webappPath)){
			packagePath = filePath.substring(filePath.indexOf(webappPath)
					+ webappPath.length(), filePath.length());
		//其他目录文件不拷贝到目标文件夹,返回null
		}else{
			return null;
		}
		
		return packagePath;
	}

	/**
	 * 
	 * @Description: 根据svnlog的配置，生成目标文件路径
	 * @param filePath
	 * @return     
	 * @author 27900
	 * @date 2016-1-28 上午11:01:04
	 */
	public String buildTargetFilePath(String filePath){
		String targetFile="";
		String packagePath="";
		String fileName = "";
		String fileType = "";
		
		packagePath = getPackageFilePath(filePath);
		
		if(packagePath==null){
			return null;
		}
		
		fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath
				.length());
		fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName
				.length());
		
		//根据package路径来构建文件路径
		//.class源文件类型
		if(filePath.contains(srcPath)){
			if ("java".equals(fileType)) {
				targetFile = TARGET_PATH + APP_NAME + "/WEB-INF/classes/"
				+ packagePath.replace(".java", ".class");
			} else {
				targetFile = TARGET_PATH + APP_NAME + "/WEB-INF/classes/"
						+ packagePath;
			}
		//资源文件类型
		}else if(filePath.contains(resourcesPath)){
			targetFile = TARGET_PATH + APP_NAME + "/WEB-INF/classes/"
			+ packagePath;
		//webapp文件类型
		}else if(filePath.contains(webappPath)){
			targetFile = TARGET_PATH + APP_NAME + "/" + packagePath;
		}
		
		System.out.println("文件名：" + fileName);
		System.out.println("文件类型：" + "." + fileType);
		System.out.println("文件packagePath: " + packagePath);
		
		return targetFile;
		
	}
	
	
	
	/**
	 * 
	 * @Description: 根据svnlog的配置，生成源文件路径
	 * @param packagePath
	 * @return     
	 * @author 27900
	 * @date 2016-1-28 上午11:01:04
	 */
	public String buildSourceFilePath(String filePath){
		String targetFile="";
		String sourceFile="";
		String packagePath="";
		String fileName = "";
		String fileType = "";
		
		packagePath = getPackageFilePath(filePath);
		
		if(packagePath==null){
			return null;
		}
		
		fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath
				.length());
		fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName
				.length());
		
		//根据package路径来构建文件路径
		//.class源文件类型
		if(filePath.contains(srcPath)){
			if ("java".equals(fileType)) {
				sourceFile = TOMCAT_PATH + APP_NAME + "/WEB-INF/classes/"
						+ packagePath.replace(".java", ".class");
			// mybatis映射文件
			} else {
				sourceFile = TOMCAT_PATH + APP_NAME + "/WEB-INF/classes/"
						+ packagePath;
			}
		//资源文件类型
		}else if(filePath.contains(resourcesPath)){
				sourceFile = TOMCAT_PATH + APP_NAME + "/WEB-INF/classes/"
				+ packagePath;
		//webapp文件类型
		}else if(filePath.contains(webappPath)){
			sourceFile = TOMCAT_PATH + APP_NAME + "/" + packagePath;
		}
		System.out.println("文件名：" + fileName);
		System.out.println("文件类型：" + "." + fileType);
		System.out.println("文件packagePath: " + packagePath);
		return sourceFile;
		
		
	}
	
	/**
	 * 
	 * @author JSean
	 * @Description 创建文件夹
	 * @param dir
	 */
	public static void makeDir(File dir) {
		if (!dir.getParentFile().exists()) {
			makeDir(dir.getParentFile());
		}
		dir.mkdir();
	}

	/**
	 * 
	 * @author JSean
	 * @Description 创建文件
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Boolean createFile(File file) throws IOException {
		if (!file.exists()) {
			makeDir(file.getParentFile());
		}
		return file.createNewFile();
	}

	/**
	 * 
	 * @author JSean
	 * @Description 复制文件
	 * @param sourceFilePath
	 * @param targetFilePath
	 * @return
	 */
	@SuppressWarnings("resource")
	public Boolean copyFile(String sourceFilePath, String targetFilePath) {
		Boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		File targetFile = new File(targetFilePath);
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			if (!sourceFile.exists()) {
				System.out.println("源文件不存在！sourceFile=" + sourceFile);
				return false;
			}
			if (!targetFile.exists()) {
				if (!createFile(targetFile)) {
					return false;
				}
			}
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(targetFile);
			byte[] b = new byte[1024];
			int res = -1;
			while ((res = fis.read(b)) != -1) {
				fos.write(b, 0, res);
			}
			fos.flush();
			fos.close();
			flag = true;
		} catch (FileNotFoundException e) {
			System.out.println("文件不存在！错误信息：" + e);
			e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			System.out.println("IO错误！错误信息：" + e);
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	public static void main(String[] args) {
		PatchBuilder patchBuilder = new PatchBuilder();
		long startTime = System.currentTimeMillis();
		patchBuilder.buildPatch();
		long endTime = System.currentTimeMillis();
		System.err.println("build sussess! 耗时：" + (endTime - startTime) + "ms");
	}

}
