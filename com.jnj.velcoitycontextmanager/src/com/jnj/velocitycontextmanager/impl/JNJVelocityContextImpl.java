package com.jnj.velocitycontextmanager.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnj.velocitycontextmanager.service.JNJVelocityContextService;
import com.polarion.core.util.logging.Logger;

public class JNJVelocityContextImpl implements JNJVelocityContextService {

	private static String POLARION_JSON_DIR = "JSON Configuration";
	private static String POLARION_JSON_DIR_PATH = System.getProperty("com.polarion.home") + "/../scripts/" + "/"
			+ POLARION_JSON_DIR + "/";
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger log = Logger.getLogger(JNJVelocityContextImpl.class);
	public Map<Integer, Map<String, Object>> jsonFilesMapObj = new HashMap<>();

	/**
	 * This method retrieves the JSON files from a specified directory, processes
	 * them, and returns a JSON response containing the names of the files. It
	 * returns a JSON object with the key "jsonFilesMapObj" that contains the
	 * processed file data.
	 * 
	 * @return void The response contains the key "jsonFilesMapObj", which holds a
	 *         map of file data.
	 */
	@Override
	public void getPolarionJSONFiles(HttpServletRequest req, HttpServletResponse resp) {
		File polarionJSONFiles = new File(POLARION_JSON_DIR_PATH);
		try {
			if (polarionJSONFiles.exists() && polarionJSONFiles.isDirectory()) {
				AtomicInteger id = new AtomicInteger(0);
				Arrays.stream(polarionJSONFiles.listFiles()).forEach(jsonFile -> {
					try {
						jsonFilesMapObj.computeIfAbsent(id.get(), k -> new LinkedHashMap<>());
						jsonFilesMapObj.get(id.get()).put("jsonFileName", jsonFile.getName());
						id.getAndIncrement();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				Map<String, Object> responseObject = new LinkedHashMap<>();
				responseObject.put("jsonFilesMapObj", jsonFilesMapObj);
				String jsonResponse = objectMapper.writeValueAsString(responseObject);

				resp.setContentType("application/json");
				resp.getWriter().write(jsonResponse);
			} else {
				log.error("The specified folder does not exist or is not a directory.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method handles the uploading of a JSON file through an HTTP request. It
	 * retrieves the file from the request, ensures the destination directory
	 * exists, writes the content of the uploaded file to the specified location,
	 * and sends a success message back to the client in JSON format.
	 */
	@Override
	public void uploadJSONFile(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Part filePart = req.getPart("jsonFile");
		String uploadedFileName = filePart.getSubmittedFileName();

		// Ensure the target directory exists
		File destinationDir = new File(POLARION_JSON_DIR_PATH);
		if (!destinationDir.exists()) {
			boolean created = destinationDir.mkdirs();
			if (created) {
				System.out.println("Directory created: " + destinationDir.getAbsolutePath());
			} else {
				System.err.println("Failed to create directory: " + destinationDir.getAbsolutePath());
				return;
			}
		}

		// Define the final file path
		File destinationFile = new File(destinationDir, uploadedFileName);

		// Write the uploaded file content to the destination file
		try (InputStream fileContent = filePart.getInputStream();
				FileOutputStream fos = new FileOutputStream(destinationFile)) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = fileContent.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}

			resp.setContentType("application/json");
			resp.getWriter().write("{\"message\": \"File uploaded successfully: " + uploadedFileName + "\"}");

		} catch (Exception e) {
			System.out.println("Errror Message is" + e.getMessage());
		}
	}

}
