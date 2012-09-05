package me.jor.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
/**
 * 
 * excel常用操作
 * 如果创建了非只读的对象，一定要调用write()方法，否则会出问题
 * 调用了write()不必再调用close()
 */
public class ExcelUtil {
	private Workbook book;
	private WritableWorkbook writablebook;
	
	/**
	 * 根据文件路径和名称建立WritableWorkbook对象
	 * @param excelpath 文件路径和名称
	 * @throws Exception 
	 */
	public ExcelUtil(String excelpath) throws Exception{
		this(new File(excelpath));
	}
	/**
	 * 根据文件路径和名称建立WritableWorkbook对象
	 * @param excelpath 文件路径和名称
	 * @param writableOnly 对象是否存在false不存在,true存在
	 * @throws Exception 
	 */
	public ExcelUtil(String excelpath, boolean writableOnly) throws Exception{
		this(new File(excelpath),writableOnly);
	}
	/**
	 * 根据文件路径和名称File对象建立WritableWorkbook对象
	 * @param excelfile 文件路径和名称File对象
	 * @param writableOnly  是否可读写的excel对象；true:只写，false:可读可写
	 * @throws IOException
	 * @throws BiffException
	 */
	public ExcelUtil(File excelfile, boolean writableOnly) throws Exception{
		boolean thrown=false;
		try{
			if(writableOnly){
				writablebook=Workbook.createWorkbook(excelfile);
				writablebook.createSheet("sheet1", 0);
			}else{
				book=Workbook.getWorkbook(excelfile);
				writablebook=Workbook.createWorkbook(excelfile,book);
			}
		}catch(Exception e){
			thrown=true;
			throw e;
		}finally{
			if(thrown){
				close();
			}
		}
	}
	/**
	 * 根据文件路径和名称建立WritableWorkbook对象,并指定文件操作类型
	 * @param excelpath excel文件路径和名称
	 * @param type 建立excel操作类型READ_ONLY:只读,WRITE_ONLY:只写,READ_WRITE:读写
	 * @throws Exception 
	 */
	public ExcelUtil(String excelpath, ExcelOperationType type) throws Exception{
		this(new File(excelpath),type);
	}
	/**
	 * 根据文件路径和名称File对象建立WritableWorkbook对象,并指定文件操作类型
	 * @param excelpath excel文件路径和名称
	 * @param type 对excel操作类型READ_ONLY:只读,WRITE_ONLY:只写,READ_WRITE:读写
	 * @throws BiffException
	 * @throws IOException
	 */
	public ExcelUtil(File excelfile, ExcelOperationType type) throws Exception{
		boolean thrown=false;
		try{
			switch(type){
			case READ_ONLY:
				book=Workbook.getWorkbook(excelfile);
				break;
			case WRITE_ONLY:
				writablebook=Workbook.createWorkbook(excelfile);
				writablebook.createSheet("sheet1", 0);
				break;
			case READ_WRITE:
				book=Workbook.getWorkbook(excelfile);
				writablebook=Workbook.createWorkbook(excelfile,book);
				break;
			}
		}catch(Exception e){
			thrown=true;
			throw e;
		}finally{
			if(thrown){
				close();
			}
		}
	}
	/**
	 * 使用输出流建立WritableWorkbook对象
	 * @param out 输入流对象
	 * @throws IOException
	 * @throws WriteException 
	 */
	public ExcelUtil(OutputStream out) throws Exception{
		boolean thrown=false;
		try{
			writablebook=Workbook.createWorkbook(out);
			writablebook.createSheet("sheet1", 0);
		}catch(Exception e){
			thrown=true;
			throw e;
		}finally{
			if(thrown){
				close();
			}
		}
	}
	/**
	 * 创建可读写的excel对象
	 * @param excelfile
	 * @throws Exception 
	 */
	public ExcelUtil(File excelfile) throws Exception{
		this(excelfile,false);
	}
	/**
	 * 使用输入流创建Workbook对象
	 * @param in 输入流
	 * @throws BiffException
	 * @throws IOException
	 */
	public ExcelUtil(InputStream in) throws Exception{
		boolean thrown=false;
		try{
			book=Workbook.getWorkbook(in);
		}catch(Exception e){
			thrown=true;
			throw e;
		}finally{
			if(thrown){
				close();
			}
		}
	}
	/**
	 * 得到序号为sheetIndex的行数
	 * @param sheetIndex sheet序号
	 * @return int 得到行数
	 * @throws 
	 * @exception
	 */
	public int getSheetRows(int sheetIndex){
		return writablebook.getSheet(sheetIndex).getRows();
	}
	/**
	 * 修改指定sheet的行和列的内容
	 * @param content 修改内容
	 * @param sheetIndex sheet序列号
	 * @param row 行
	 * @param column 列
	 * @throws RowsExceededException
	 * @throws WriteException void
	 * @throws 
	 * @exception
	 */
	public void modify(String content, int sheetIndex, int row, int column) throws RowsExceededException, WriteException{
		WritableSheet sheet=writablebook.getSheet(sheetIndex);
		Cell cell=sheet.getCell(column, row);
		Label label=new Label(column,row,content);
		if(cell!=null){
			CellFormat cf=cell.getCellFormat();
			if(cf!=null){
				label.setCellFormat(cf);
			}
		}
		sheet.addCell(label);
	}
	/**
	 * 修改指定sheet的行和列的内容
	 * @param content 修改内容
	 * @param sheetIndex sheet序号
	 * @param row 行
	 * @param column 列
	 * @param cellFormat 
	 * @throws RowsExceededException
	 * @throws WriteException void
	 * @throws 
	 * @exception
	 */
	public void modify(String content,int sheetIndex,int row, int column, CellFormat cellFormat) throws RowsExceededException, WriteException{
		WritableSheet sheet=writablebook.getSheet(sheetIndex);
		Cell cell=sheet.getCell(column, row);
		Label label=new Label(column,row,content);
		label.setCellFormat(cellFormat);
		sheet.addCell(label);
	}
	public void modify(int sheetIndex, Label label) throws RowsExceededException, WriteException, IndexOutOfBoundsException{
		writablebook.getSheet(sheetIndex).addCell(label);
	}
	/**
	 * 创建Sheet
	 * @param name sheet名称
	 * @param idx void sheet序号
	 * @throws 
	 * @exception
	 */
	public void createSheet(String name, int idx){
		writablebook.createSheet(name, idx);
	}
	/**
	 * 删除sheet
	 * @param idx void sheet序号
	 * @throws 
	 * @exception
	 */
	public void removeSheet(int idx){
		writablebook.removeSheet(idx);
	}
	/**
	 * 设置行高
	 * @param sheetIndex sheet序号
	 * @param row 行
	 * @param height 高度
	 * @throws RowsExceededException
	 * @throws IndexOutOfBoundsException void
	 * @throws 
	 * @exception
	 */
	public void setRowHeight(int sheetIndex, int row, int height) throws RowsExceededException, IndexOutOfBoundsException{
		writablebook.getSheet(sheetIndex).setRowView(row,height);
	}
	/**
	 * 设置列宽
	 * @param sheetIndex sheet序号
	 * @param column 列
	 * @param width void 宽度
	 * @throws 
	 * @exception
	 */
	public void setColumnWidth(int sheetIndex, int column, int width){
		writablebook.getSheet(sheetIndex).setColumnView(column, width);
	}
	/**
	 * 向excel文件写内容，并关闭ExcelUtil占用的所有资源。调用了此方法不必再调用close()。
	 * @throws IOException
	 * @throws WriteException void
	 * @throws 
	 * @exception
	 */
	public void write() throws IOException, WriteException{
		try{
			writablebook.write();
		}finally{
			close();
		}
	}
	/**
	 * 读取excel内容，并由回调函数callback进行处理
	 * @param sheetIndex sheet序号
	 * @param ignoreStartLines 读取数据的开始行
	 * @param callback void 读取数据后的回调函数
	 * @throws 
	 * @exception
	 */
	public void read(int sheetIndex, int ignoreStartLines, ReadCallback callback){
		Sheet sheet=read(sheetIndex);
		int rows=sheet.getRows();
		for(int i=ignoreStartLines;i<rows;i++){
			callback.execute(sheet.getRow(i));
		}
	}
	/**
	 * 迭代整个workbook的所有sheet
	 * @param callback 迭代回调接口
	 */
	public void iterateEachSheet(IterateSheet callback, boolean iterateAllSheet){
		Sheet[] sheets=book.getSheets();
		for(int i=0,l=sheets.length;i<l;i++){
			if(!iterateRows(sheets[i],callback,iterateAllSheet)){
				return;
			}
		}
	}
	private boolean iterateRows(Sheet sheet, IterateSheet callback, boolean iterateAllSheet){
		int rows=sheet.getRows();
		for(int r=0;r<rows;r++){
			if(!callback.execute(r, sheet.getRow(r))){
				if(iterateAllSheet){
					return true;
				}else{
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 得到sheet对象
	 * @param sheetIndex sheet序号
 	 * @return Sheet
	 * @throws 
	 * @exception
	 */
	public Sheet read(int sheetIndex){
		return book.getSheet(sheetIndex);
	}
	/**
	 * 
	 * @param name   sheet名称
	 * @return Sheet
	 */
	public Sheet read(String name){
		return book.getSheet(name);
	}
	/**
	 * 读取指定行中各列数据
	 * @param sheet sheet对象
	 * @param row 行
	 * @return Cell[] 行中列数据数组 
	 * @throws 
	 * @exception
	 */
	public Cell[] read(Sheet sheet, int row){
		return sheet.getRow(row);
	}
	/**
	 * 关闭Workbook writablebook对象
	 * @throws WriteException
	 * @throws IOException void
	 * @throws 
	 * @exception
	 */
	public void close(){
		try{
			if(writablebook!=null){
				writablebook.close();
			}
		}catch(Exception e){
		}finally{
			writablebook=null;
		}
		try{
			if(book!=null){
				book.close();
			}
		}catch(Exception e){
		}finally{
			book=null;
		}
	}
	/**
	 * 
	 * 读取excel内容后的处理类的接口，实现此接口来对读取的行中各列的数据进行处理
	 *
	 */
	public static interface ReadCallback{
		public void execute(Cell[] cells);
	}
	/**
	 * 迭代读取指定的sheet所有内容
	 * 方法参数是要迭代的sheet中的每一行单元格
	 */
	public static interface IterateSheet{
		public boolean execute(int row, Cell[] cells);
	}
	/**
	 * 
	 * 对excel操作的枚举
	 *
	 */
	public static enum ExcelOperationType{
		READ_ONLY, WRITE_ONLY, READ_WRITE;
	}
}
