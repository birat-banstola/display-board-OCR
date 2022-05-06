package ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.*;
import java.io.*;
import org.apache.poi.xwpf.usermodel.*;

public class DoingOcr {

	ITesseract ins;
	String res;
	public DoingOcr()
	{
		ins=new Tesseract();
		//ins.setDatapath("C:\\Users\\ChanakyaS\\workspace\\ocr\\tessdata");
		ins.setDatapath(System.getProperty("user.dir")+"\\tessdata");
		res=new String("");
	}
	
	public String giveOcrResult(File files[])throws TesseractException
	{
		for(File file:files)
		{
			
			res=res+ins.doOCR(file);
		}
		return res;
	}
	
	public String giveOcrResult(File file)throws TesseractException
	{
		res=res+ins.doOCR(file);
		return res;
	}
	
	public XWPFDocument giveOcrResult(File files[], XWPFDocument document)
			throws TesseractException
	{
		for(File file : files){
		res= ins.doOCR(file);
		XWPFParagraph paragraph = document.createParagraph();
	    XWPFRun run = paragraph.createRun();
	    run.setText(res);
		}
		return document;
	}
	
}
