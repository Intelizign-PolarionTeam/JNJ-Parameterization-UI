package com.jnj.velocitycontextmanager.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jnj.velocitycontextmanager.service.JNJVelocityContextService;
import com.polarion.alm.projects.model.IFolder;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IModule;
import com.polarion.alm.tracker.model.IModuleAttachment;
import com.polarion.core.util.logging.Logger;
import com.polarion.platform.core.PlatformContext;
import com.polarion.platform.persistence.model.IPObjectList;
import com.polarion.platform.persistence.model.IPrototype;

public class JNJVelocityContextImpl implements JNJVelocityContextService {

	private static String POLARION_JSON_DIR = "JSON Configuration";
	private static String POLARION_JSON_DIR_PATH = System.getProperty("com.polarion.home") + "/../scripts/" + "/"
			+ POLARION_JSON_DIR + "/";
	private static final ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
			.lookupService(ITrackerService.class);
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
	/*@Override
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
	}*/

	
	@Override
	public void uploadJSONFile(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// TODO Auto-generated method stub
		try {
			String projectId = "Innomedic";
		    List<IFolder> folderList = trackerService.getFolderManager().getFolders(projectId);
		    
		    for (IFolder folder : folderList) {
		        IPrototype modulePrototype = trackerService.getDataService().getPrototype("Module");
		        IPObjectList pObjectList = trackerService.getDataService().searchInstances(modulePrototype, projectId, folder.getName());

		        for (Object obj : pObjectList) {
		            if (obj instanceof IModule) {
		                IModule module = (IModule) obj;
		              //  System.out.println("Checking Module: " + module.getModuleName());

		                try {
		                    IPObjectList<IModuleAttachment> moduleAttachments = module.getAttachments();
		                    boolean hasJson = false;

		                    for (IModuleAttachment attachment : moduleAttachments) {
		                        String attachmentFileName = attachment.getFileName();

		                        if (attachmentFileName != null && attachmentFileName.toLowerCase().endsWith(".json")) {
		                            hasJson = true;

		                            System.out.println("---- Found JSON Attachment -----");
		                            System.out.println("Module: " + module.getModuleName() + 
		                                           " | Attachment File: " + attachmentFileName);

		                            // Read the content from InputStream
		                            try (InputStream inputStream = attachment.getDataStream()) {
		                                String content = new BufferedReader(new InputStreamReader(inputStream))
		                                        .lines().collect(Collectors.joining("\n"));
		                                //System.out.println("JSON Content:\n" + content);

		                                // You can now modify `content` and write it back if your system supports it.
		                            } catch (Exception ex) {
		                                System.out.println("Error reading InputStream for attachment: " + ex.getMessage());
		                            }
		                        }
		                    }

		                    if (!hasJson) {
		                        //System.out.println("No JSON attachments found for module: " + module.getModuleName());
		                    }

		                } catch (Exception e) {
		                    System.out.println("Error processing module '" + module.getModuleName() + "': " + e.getMessage());
		                }
		            }
		        }
		    }

		} catch (Exception e) {
		    log.error("Error reading JSON: " + e.getMessage());
		    e.printStackTrace();
		}
		
	}


	
}