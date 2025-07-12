package com.product.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.product.entity.Product;
import com.product.repo.ProductRepo;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ProductService {
	@Autowired
	ProductRepo repo;

	public List<List<String>> readExcel(MultipartFile file) throws IOException {
		List<List<String>> data = new ArrayList<>();
		try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
			org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0); // Read the first sheet
			for (Row row : sheet) 
			{
				List<String> rowData = new ArrayList<>();
				for (Cell cell : row) {
					rowData.add(getCellValue(cell));
				}
				data.add(rowData);
			}
		}
		return data;
	}

	private String getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			} else {
				return String.valueOf(cell.getNumericCellValue());
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		default:
			return "";
		}
	}

	public void saveExcelData(List<List<String>> tableData) {
		for (List<String> row : tableData) {
			Product product = new Product();

			product.setProductId(row.get(0));
			product.setProductName(row.get(1));
			product.setProductType(row.get(2));
			product.setProductPrice(row.get(3));

			repo.save(product);
		}
	}

	public List<Product> searchProducts(String productName, String productType) {
		if (productName != null && productType != null) {
			return repo.findByProductNameAndProductType(productName, productType);
		}
		else if(productName !=null)
		{
			return repo.findByProductName(productName);
		}
		else if(productType !=null)
		{
			return repo.findByProductType(productType);
		}else {
			return repo.findAll(); // No filter applied
		}
	}

	public void exportFilteredProductData(HttpServletResponse response, String productName, String productType)
			throws Exception {
		List<Product> products;
		if (productName != null && productType != null) {
			products=repo.findByProductNameAndProductType(productName, productType);
		}
		else if(productName !=null)
		{
			products =repo.findByProductName(productName);
		}
		else if(productType !=null)
		{
			products= repo.findByProductType(productType);
		}
		else {
			products= repo.findAll(); // No filter applied
		}
		System.err.println(productType+"-----------------"+productName);
			
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Filtered Products");

		// Add header row
		HSSFRow headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("ProductId");
		headerRow.createCell(1).setCellValue("ProductName");
		headerRow.createCell(2).setCellValue("ProductType");
		headerRow.createCell(3).setCellValue("ProductPrice");

		// Populate data rows
		int rowNum = 1;
		for (Product product : products) {
			HSSFRow dataRow = sheet.createRow(rowNum++);
			dataRow.createCell(0).setCellValue(product.getProductId());
			dataRow.createCell(1).setCellValue(product.getProductName());
			dataRow.createCell(2).setCellValue(product.getProductType());
			dataRow.createCell(3).setCellValue(product.getProductPrice());
			rowNum++;
		}

		// Write the workbook to the response output stream
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

}
