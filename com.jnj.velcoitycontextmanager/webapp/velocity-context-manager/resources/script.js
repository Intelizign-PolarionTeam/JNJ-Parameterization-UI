var liveDocList;
$(document).ready(function() {
	$.ajax({
		url: 'velocitycontextmanager?action=getLiveDocAttachedWithJson',
		type: 'GET',
		dataType: 'json',
		success: function(response) {
			if (response) {
				liveDocList = response.liveDocAttachmentInfo;
				liveDocList.forEach((docInfo, index) => {
					const moduleName = docInfo.moduleName;
					const fileName = docInfo.fileName;

					let row = '<tr class="polarion-rpw-table-content-row">';
					row += '<td style="width:40%">' + moduleName + '</td>';
					row += '<td style="width:40%">' + fileName + '</td>';
					row += '<td style="width:20%"><button class="edit-json-file-btn" data-index="' + index + '">Edit</button></td>';
					row += '</tr>';

					$('#json-file-body').append(row);
					$(document).on('click', '.edit-json-file-btn', function() {
						const index = $(this).data('index');
						const doc = liveDocList[index];
						console.log("Edit Its working");
						//alert('Editing:\nModule: ' + doc.moduleName + '\nFile: ' + doc.fileName + '\nContent:\n' + doc.content);

						$('#editor-module-name').text(doc.moduleName);
						$('#editor-file-name').text(doc.fileName);
						$('#json-textarea').val(doc.content);
						//$('#json-editor').data('index', index).show();
						$('#main-container').css('display', 'none');
						$('#json-editor').css('display', 'block').data('index', index);

					});

				});
			} else {
				console.error('Failed to retrieve live document list');
			}

		},
		error: function(error) {
			console.error('Error occurred while fetching project list:', error);
		}
	});
});

// Function to trigger the file input click when the button is clicked
function triggerFileInput() {
	const fileInput = document.getElementById('file-input');
	fileInput.click();
}


/*function editJsonFile() {
	const index = $(this).data('index');
	const doc = liveDocList[index];
	alert('Editing:\nModule: ' + doc.moduleName + '\nFile: ' + doc.fileName + '\nContent:\n' + doc.content);
	$('#editor-module-name').text(doc.moduleName);
	$('#editor-file-name').text(doc.fileName);
	$('#json-textarea').val(doc.content);
	$('#json-editor').data('index', index).show();

}*/


function closeJsonFile() {
	$('#json-editor').css('display', 'none');
	$('#main-container').css('display', 'block');
 }
// On Save Click
function saveJsonFile() {
	const index = $('#json-editor').data('index');
	const updatedContent = $('#json-textarea').val();
	
	liveDocList[index].content = updatedContent;

	const updatedData = {
		moduleName: liveDocList[index].moduleName,
		fileName: liveDocList[index].fileName,
		content: updatedContent
	};

	$.ajax({
		url: 'velocitycontextmanager?action=updateLiveDocAttachment',
		method: 'POST',
		contentType: 'application/json',
		data: JSON.stringify(updatedData),
		success: function(response) {
			alert('JSON updated successfully!');
			$('#json-editor').hide();
		    $('#main-container').css('display', 'block');
		},
		error: function(err) {
			console.error('Error updating JSON:', err);
			alert('Failed to update JSON.');
		}
	});
}

// Function to handle the file selection and upload
function handleFileUpload(event) {
	// Get the selected file
	const file = event.target.files[0];
	if (file) {
		console.log('Selected file:', file.name);

		// Create FormData object
		const formData = new FormData();
		formData.append('jsonFile', file);


		$.ajax({
			url: 'velocitycontextmanager?action=uploadJSONFile',
			type: 'POST',
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				if (response) {
					console.log("Json File uploaded Sucessfully");
				} else {
					console.error('Issue on File Uploading');
				}
			},
			error: function(error) {
				console.error('Error occurred while fetching project list:', error);
			}
		});
	}
}

