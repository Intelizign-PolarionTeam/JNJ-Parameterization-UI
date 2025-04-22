const sampleData = {
	"Innomedic": [
		{ moduleName: "Hazard RiskSheet", fileName: "riskParameter.json" },
		{ moduleName: "Security Risksheet", fileName: "riskParameter.json" },
		{ moduleName: "Harm RiskSheet", fileName: "riskParameter.json" },
		{ moduleName: "Cause ModeSheet", fileName: "riskParameter.json" },
		{ moduleName: "P1 Modesheet", fileName: "riskParameter.json" },
		{ moduleName: "P2 Mode Sheet", fileName: "riskParameter.json" }
	],
	"Demo 123": [
		{ moduleName: "Cause Mode RiskSheet", fileName: "riskParameter.json" },
		{ moduleName: "Cause 2 Mode RiskSheet", fileName: "riskParameter.json" }
	]
};

const jsonData = {
	"columns": [
		{
			"headerGroup": "Final ranking",
			"headerGroupCss": "headFinalRanking",
			"headerCss": "headFinalRanking",
			"header": "RPN",
			"bindings": "rpnNew",
			"id": "rpnNew",
			"type": "int",
			"cellRenderer": "rpn",
			"filterable": true,
			"width": 110,
			"level": 3,
			"collapseTo": true,
			"formula": "commonRpnNew"
		}
	],
	"levels": [
		{
			"name": "Item",
			"controlColumn": "item",
			"zoomColumn": "item"
		},
		{
			"name": "Failure mode",
			"controlColumn": "failureMode",
			"zoomColumn": "failureMode"
		},
		{
			"name": "Cause",
			"zoomColumn": "causes",
			"controlColumn": "systemItemId"
		}
	],
	"dataTypes": {
		"risk": {
			"type": "risk"
		},
		"task": {
			"type": "task",
			"role": "mitigates",
			"name": "Task",
			"zoomColumn": "taskTitle"
		}
	},
	"sortBy": ["item", "failureMode", "causes"]
};

let currentEditingIndex = null;


function openEditor(content, index) {
	document.getElementById("parameter-div").style.display = "none";
	document.getElementById("editor").style.display = "block";
	document.getElementById("editContent").value = content;
	currentEditingIndex = index;
}

function saveEditedContent() {
	alert(`Parameter File Updated Sucessfully`);
}

function cancelEdit() {
	document.getElementById("editor").style.display = "none";
	document.getElementById("parameter-div").style.display = "block";
	currentEditingIndex = null;
}


function handleApply() {
	const tbody = document.getElementById("resultsBody");
	tbody.innerHTML = ""; // Clear previous results

	Object.entries(sampleData).forEach(([moduleName, attachments]) => {
		attachments.forEach(file => {
			const row = document.createElement("tr");

			row.innerHTML = `
          <td>${file.moduleName}</td>
           <td>Risk</td>
          <td>${file.fileName}</td>
          <td>
            <button id="edit-btn" onclick="editFile()">Edit</button>
          </td>
        `;

			tbody.appendChild(row);
		});
	});

	document.getElementById("resultsTable").style.display = "block";
}

function editFile() {
	    const jsonDataContent = JSON.stringify(jsonData, null, 2);
		openEditor(jsonDataContent, 0);
}


