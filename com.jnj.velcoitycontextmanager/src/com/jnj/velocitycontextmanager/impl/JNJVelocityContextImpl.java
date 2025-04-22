package com.jnj.velocitycontextmanager.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnj.velocitycontextmanager.model.LiveDocAttachmentInfo;
import com.jnj.velocitycontextmanager.service.JNJVelocityContextService;
import com.polarion.alm.projects.model.IFolder;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IModule;
import com.polarion.alm.tracker.model.IModuleAttachment;
import com.polarion.core.util.logging.Logger;
import com.polarion.platform.ITransactionService;
import com.polarion.platform.core.PlatformContext;
import com.polarion.platform.persistence.model.IPObjectList;
import com.polarion.platform.persistence.model.IPrototype;

public class JNJVelocityContextImpl implements JNJVelocityContextService {
	private static final ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
			.lookupService(ITrackerService.class);
	private ITransactionService transactionservice = (ITransactionService) PlatformContext.getPlatform()
			.lookupService(ITransactionService.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger log = Logger.getLogger(JNJVelocityContextImpl.class);
	public Map<Integer, Map<String, Object>> jsonFilesMapObj = new HashMap<>();
	public List<LiveDocAttachmentInfo> docAttachmentInfoList = new ArrayList<>();

	@Override
	public void getLiveDocAttachedWithJsonFile(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		try {
			String projectId = "Innomedic";
			List<IFolder> folderList = trackerService.getFolderManager().getFolders(projectId);

			// Flat list to store unique LiveDocAttachmentInfo entries
			List<LiveDocAttachmentInfo> docAttachmentInfoList = new ArrayList<>();

			IPrototype modulePrototype = trackerService.getDataService().getPrototype("Module");

			for (IFolder folder : folderList) {
				IPObjectList pObjectList = trackerService.getDataService().searchInstances(modulePrototype, projectId,
						folder.getName());

				for (Object obj : pObjectList) {
					if (obj instanceof IModule) {
						IModule module = (IModule) obj;
						String moduleName = module.getModuleName();

						IPObjectList<IModuleAttachment> moduleAttachments = module.getAttachments();

						Set<String> addedFileNames = new HashSet<>(); 

						for (IModuleAttachment attachment : moduleAttachments) {
							String attachmentFileName = attachment.getFileName();

							if (attachmentFileName != null && attachmentFileName.toLowerCase().endsWith(".json")
									&& addedFileNames.add(attachmentFileName)) { 
								try (InputStream inputStream = attachment.getDataStream()) {
									String content = new BufferedReader(new InputStreamReader(inputStream)).lines()
											.collect(Collectors.joining("\n"));

									LiveDocAttachmentInfo docAttachmentObj = new LiveDocAttachmentInfo();
									docAttachmentObj.setContent(content);
									docAttachmentObj.setFileName(attachmentFileName);
									docAttachmentObj.setModuleName(moduleName);

									docAttachmentInfoList.add(docAttachmentObj);

								} catch (Exception ex) {
									System.out.println("Error reading attachment '" + attachmentFileName
											+ "' in module '" + moduleName + "': " + ex.getMessage());
								}
							}
						}
					}
				}
			}

			// Prepare response
			Map<String, Object> responseObject = new LinkedHashMap<>();
			responseObject.put("liveDocAttachmentInfo", docAttachmentInfoList);

			String jsonResponse = objectMapper.writeValueAsString(responseObject);

			resp.setContentType("application/json");
			resp.getWriter().write(jsonResponse);

		} catch (Exception e) {
			log.error("Error reading JSON: " + e.getMessage(), e);
			e.printStackTrace();
		}

	}

	@Override
	public void updateLiveDocAttachment(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		StringBuilder jsonBuffer = new StringBuilder();
		String line;
		try (BufferedReader reader = req.getReader()) {
			while ((line = reader.readLine()) != null) {
				jsonBuffer.append(line);
			}
		}

		String requestBody = jsonBuffer.toString();
		// Convert JSON string to Java object using Jackson
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> jsonMap = mapper.readValue(requestBody, new TypeReference<Map<String, Object>>() {
		});

		String moduleName = (String) jsonMap.get("moduleName");
		String fileName = (String) jsonMap.get("fileName");
		String content = (String) jsonMap.get("content");
		String projectId = "Innomedic";
		String documentId = moduleName;

		System.out.println("moduleName: " + moduleName);
		System.out.println("fileName: " + fileName);
		System.out.println("content: " + content);
		List<IFolder> folderList = trackerService.getFolderManager().getFolders(projectId);
		IPrototype modulePrototype = trackerService.getDataService().getPrototype("Module");
		Map<Integer, IModule> documentList = new HashMap<>();
		int itrCount = 0;
		transactionservice.beginTx();
		// Getting all Documents in Current Project
		for (IFolder folder : folderList) {
			IPObjectList pObjectList = trackerService.getDataService().searchInstances(modulePrototype, projectId,
					folder.getName());
			for (Object obj : pObjectList) {
				if (obj instanceof IModule) {
					IModule module = (IModule) obj;
					documentList.put(itrCount, module);
					itrCount++;
				}

			}
		}
		// System.out.println("DocumentList : " + documentList);
		for (Map.Entry<Integer, IModule> entry : documentList.entrySet()) {
			IModule module = entry.getValue();

			if (module.getModuleName().equals(documentId)) {
				// Found the matching module
				System.out.println("Matching module found: " + module.getModuleName());

				// Delete the old attachment if exists
				if (module.getAttachment(fileName) != null) {
					module.deleteAttachment(module.getAttachment(fileName));
					System.out.println("Old attachment deleted");
				}

				// Create the new attachment with updated content
				try (InputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
					IModuleAttachment createAttachment = module.createAttachment(fileName, null, contentStream);
					createAttachment.save();

					try {
						module.save();
						transactionservice.endTx(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Attachment updated successfully for module: " + module.getModuleName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		Map<String, String> responseObj = new HashMap<>();
		responseObj.put("status", "success");
		responseObj.put("message", "JSON updated successfully");

		String jsonResponse = mapper.writeValueAsString(responseObj);
		resp.setContentType("application/json");
		resp.getWriter().write(jsonResponse);

	}

}
