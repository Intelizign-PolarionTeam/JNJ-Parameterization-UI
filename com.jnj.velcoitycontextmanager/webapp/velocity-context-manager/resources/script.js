$(document).ready(function() {
	$.ajax({
		url: 'velocitycontextmanager?action=getPolarionJSONResources',
		type: 'GET',
		dataType: 'json',
		success: function(response) {
			if (response) {
				const jsonFilesMapObj = response.jsonFilesMapObj;
				Object.keys(jsonFilesMapObj).forEach(function(key) {
					var jsonFileName = jsonFilesMapObj[key].jsonFileName;
					var row = '<tr class="polarion-rpw-table-content-row">';
					row += '<td style="width:50%">' + jsonFileName + '</td>';
					row += '<td style="width:50%;"><button id="edit-json-file-btn">Edit</button></td>';
                    row += '</tr>';
					$('#json-file-body').append(row);
				});
			} else {
				console.error('Failed to retrieve project list');
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

