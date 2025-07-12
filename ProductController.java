package com.product.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.product.entity.Product;
import com.product.service.ProductService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProductController {
	@Autowired
	ProductService service;
	
	
	
	@GetMapping("/")
	public String showHome() {
		return "home";
	}
	
	@GetMapping("/upload")
	public String productUpload() {
		return "upload";
	}

	
	 @PostMapping("/upload")
	    public String handleFileUpload(MultipartFile file, Model model) {
	        if (file.isEmpty()) {
	            model.addAttribute("error", "Please select an Excel file to upload.");
	            return "upload";
	        }

	        try {
	            List<List<String>> data = service.readExcel(file);
	            model.addAttribute("data", data);
	            service.saveExcelData(data);
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("error", "Failed to read Excel file: " + e.getMessage());
	        }

	        return "upload";
	    }
	 @GetMapping("/export")
	 public void genarteExcelreport(HttpServletResponse response,@RequestParam(value = "productName", required = false) String productName,
		        @RequestParam(value = "productType", required = false) String productType ) throws Exception
	 {
		 response.setContentType("application/octet-stream");
		 String  headerkey="Content-Disposition";
		 String headerValue="attachment;filename=products.xls";
		 response.setHeader(headerkey, headerValue);
		 service.exportFilteredProductData(response, productName, productType);
		 
	 }
	
	  
	 @GetMapping("/search")
	    public String searchProduct(@RequestParam(value = "productName", required = false) String productName,
	                                @RequestParam(value = "productType", required = false) String productType,
	                                Model model){

	        List<Product> products = service.searchProducts(productName, productType);
	        model.addAttribute("products", products);
	        

	        return "search"; // Replace with your Thymeleaf template name
	    }
	 
}
