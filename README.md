#Roadrunner的使用
----
## 使用方法
1. 将工程导入workspace
2. 在自己的工程中导入roadrunner.jar
3. 在将项目foruse/文件夹下的三个文件夹：etc/,stype/,output/放到自己的工程目录下（及eclipse项目的.project文件所在的目录）。
4. 导入包名`com.wolf_datamining.autoextracting.roadrunner.application`使用接口下面讲解的接口进行开发。



## 代码讲解
注意，并不是下面所有的代码都要用，请根据需要使用。

### 文件转换
		
<pre>	
//  首先从html到xhtml的文件转换
//  使用nekohtml
Html2XhtmlByNeko converter = new Html2XhtmlByNeko();

// 设置输出文件编码
//converter.setEncoding("utf8"); // 输出编码的设置，如果不设置将采用输入编码
// 设置输入文件的编码
converter.setInputEncoding("utf8"); // 如果HTML没有声明编码，这个一定要设置，否则不需要设置

// 单文件转换方法
// arg1: 输入文件路径，包含文件名
// arg2: 输出文件路径，包含文件名
converter.convert("C:/Users/Admin/Desktop/test3.html", 
		"C:/Users/Admin/Desktop/test33.xhtml");

// 文件批量转换
// arg1: 输入文件路径，到文件夹
// arg2: 输出文件路径，到文件夹
converter.convertAll("C:/Users/Admin/Desktop/test/",
		"C:/Users/Admin/Desktop/result/");
</pre>


### 生成包装器

<pre>
// 实例化抽取器，抽取器有直接从html文件操作和从xhtml文件操作两种。
//ExtractFromHtml extracter = new ExtractFromHtml();
ExtractFromXhtml extracter = new ExtractFromXhtml();

// 设置输入文件的编码格式
extracter.setInputFileEncoding("gb2312");

//设置配置文件路径，不设置使用默认配置文件。
extracter.setConfigPath("path");
	
// 生成包装器
extracter.generateWrapper("C:/Users/Admin/Desktop/pages/", "outdir");
</pre>
### 抽取操作	
<pre>

// 从一张页面抽取
// 参数1：包装器路径
// 参数2：输入文件路径，包含文件名
// 参数3：输出文件路径，不包含文件名
//extracter.extract("G:/datamining/Projects/roadrunnertest/output/test/newtest00.xml",
//			"C:/Users/Admin/Desktop/pages/test.html",
//			"G:/datamining/Projects/roadrunnertest/output/outdir/test");
	
// 批量抽取
// arg1: 包装器路径
// arg2: 输入文件路径，到文件所在的文件夹
// arg3: 输出文件路径，到输出文件所在的文件夹
//extracter.extractAll("G:/datamining/Projects/roadrunnertest/output/newtest/newtest00.xml",
//			"C:/Users/Admin/Desktop/test/", "G/datamining/Projects/roadrunnertest/output/newtest/");
		
</pre>


## Sample

### 将html转换为xhtml

#### 1. 单文件转换
<pre>
Html2XhtmlByNeko converter = new Html2XhtmlByNeko(); 
converter.setInputEncoding("utf-8");
converter.setOutputEncoding("utf-8");
//注意：第一个参数是url，第二个参数是路径
converter.convert("file:///F:/Workspace-Java/JavaPrac/pages/GGadagio.html",
		"F:/Workspace-Java/JavaPrac/out-pages/out.xhtml");
</pre>

#### 2. 多文件批量转换
<pre>
Html2XhtmlByNeko converter = new Html2XhtmlByNeko(); 
converter.setInputEncoding("utf-8");
converter.setOutputEncoding("utf-8");
// 两个参数都是文件夹目录
converter.convertAll("F:/Workspace-Java/JavaPrac/pages/",
	"F:/Workspace-Java/JavaPrac/out-pages/");
</pre>

### 训练Wrapper
<pre>
ExtractFromXhtml extracter = new ExtractFromXhtml();
extracter.setInputFileEncoding("gb2312");
// 第一个参数是用于训练wrapper的相似页面所在的目录，第二个参数用于创建output/文件夹下的输出目录（准备工作中在项目目录下放置的output/文件夹）。输出文件中的test00.xml文件是wrapper文件，这个文件用于后面的抽取工作。其他几个文件是数据文件，建议用ie打开index.html进行查看。
extracter.generateWrapper("F:/Workspace-Java/JavaPrac/out-pages/", 
		"test");
</pre>

### 数据抽取

#### 单独抽取
<pre>
ExtractFromXhtml extracter = new ExtractFromXhtml();
extracter.setInputFileEncoding("utf-8");
// 第一个路径是包装器的路径, 第二个参数是待抽取的文件，最后一个参数是输出文件夹
extracter.extract("F:/Workspace-Java/JavaPrac/output/yamaxun/yamaxun00.xml",
    		"F:/Workspace-Java/JavaPrac/pages/yamaxun/djangoweb.htm",
    		"F:/Workspace-Java/JavaPrac/output/");
</pre>

#### 批量抽取
<pre>
ExtractFromXhtml extracter = new ExtractFromXhtml();
extracter.setInputFileEncoding("utf-8");
// 第一个路径是包装器的路径, 第二个参数是待抽取的文件所在文件夹，最后一个参数是输出文件夹
extracter.extractAll("F:/Workspace-Java/JavaPrac/output/yamaxun/yamaxun00.xml",
		"F:/Workspace-Java/JavaPrac/pages/yamaxun/", "F:/Workspace-Java/JavaPrac/output/");	
</pre>
