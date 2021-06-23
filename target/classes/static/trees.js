// Empty JS for your own code to be here

// Treeview Initialization
//$(document).ready(function() {
//  $('.treeview-animated').mdbTreeview();
//});

$(function() {

var ruletree = [
  {
    text: "save:Rule",

    //icon: "fa fa-folder",
    nodes: [
      {
        text: "save:Permission",
        state: {
//    checked: true,
//    disabled: true,
//    expanded: true,
            selected: true
        },
        //icon: "fa fa-folder",
//        nodes: [
//          {
//            id:    "sub-node-1",
//            text:  "Sub Child Node 1",
//            //icon:  "fa fa-folder",
//            class: "nav-level-3",
//            //href:  "https://google.com"
//          },
//          {
//            text: "Sub Child Node 2",
//            //icon: "fa fa-folder"
//          }
//        ]
      },
      {
        text: "save:Prohibition",
         //icon: "fa fa-folder"
      },
      {
        text: "save:Obligation",
         //icon: "fa fa-folder"
      },
      {
        text: "save:Dispensation",
         //icon: "fa fa-folder"
      }
    ]
  },
//  {
//    text: "",
//    //icon: "fa fa-folder"
//  },
//  {
//    text: "Node 3",
//    //icon: "fa fa-folder"
//  },
//  {
//    text: "Node 4",
//    //icon: "fa fa-folder"
//  },
//  {
//    text: "Node 5",
//    //icon: "fa fa-folder"
//  }
];


$('#ruletree').treeview({
data: ruletree,
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
});


var actiontree = [
  {
    text: "save:Processing",
    //icon: "fa fa-folder",
    nodes: [
      {
        text: "save:Copy"
      },
      {
        text: "save:Disclose",
         //icon: "fa fa-folder"
         nodes: [
         { "text": "dpv:DiscloseByTransmission"
         },
         { "text": "dpv:Disseminate"
         },
         { "text": "dpv:MakeAvailable"
         },
         { "text": "dpv:Share"
         },
         { "text": "dpv:Transmit"
         },
         ]
      },
      {
        text: "save:Obtain",
        state: {
            expanded: true,
          },
         //icon: "fa fa-folder"
         nodes: [
         { "text": "dpv:Acquire"
         },
         { "text": "dpv:Collect",
         state: {
            selected: true
          },
         },
         { "text": "dpv:Record"
         },
         ]
      },
      {
        text: "save:Remove",
         //icon: "fa fa-folder"
         nodes: [
         { "text": "dpv:Destruct"
         },
         { "text": "dpv:Erase"
         },
         ]
      },
      {
        text: "save:Store",
         //icon: "fa fa-folder"
      },
      {
        text: "save:Transfer",
         //icon: "fa fa-folder"
         nodes: [
         { "text": "dpv:Move"
         },
         ]
      },
      {
        text: "save:Transform",
         //icon: "fa fa-folder"
         nodes: [
         { "text": "dpv:Adapt"
         },
         { "text": "dpv:Align"
         },
         { "text": "dpv:Alter"
         },
         { "text": "dpv:Anonymize"
         },
         { "text": "dpv:Combine"
         },
         { "text": "dpv:Derive"
         },
         { "text": "dpv:Organise",
            nodes: [
            { "text": "dpv:Structure"
            },
            ]
         },
         { "text": "dpv:PseudoAnonymize"
         },
         { "text": "dpv:Restrict"
         },
         ]
      },
      {
        text: "save:Use",
         //icon: "fa fa-folder"
         nodes: [
         { "text": "dpv:Analyse"
         },
         { "text": "dpv:Consult"
         },
         { "text": "dpv:Profile"
         },
         { "text": "dpv:Retrieve"
         },
         ]
      }
    ]
  },

];

var actions = $("#actionsGT").val();
var ds = $("#dsGT").val();

$('#actiontree').treeview({
data: $("#actionsGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
onNodeSelected: function(event, node) {
  selectedAction = node.text;
},
onNodeUnselected: function (event, node) {
  selectedAction = null;
}
});

$('#datatree').treeview({
data: $("#dataGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
onNodeSelected: function(event, node) {
  selectedData = node.text;
},
onNodeUnselected: function (event, node) {
  selectedData = null;
}
});

$('#purposetree').treeview({
data: $("#purposesGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
onNodeSelected: function(event, node) {
  selectedPurpose = node.text;
},
onNodeUnselected: function (event, node) {
  selectedPurpose = null;
}
});

$('#legalbasestree').treeview({
data: $("#legalBasesGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
onNodeSelected: function(event, node) {
  selectedLegalBasis = node.text;
},
onNodeUnselected: function (event, node) {
  selectedLegalBasis = null;
}
});

$('#controllerstree').treeview({
data: $("#controllersGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
onNodeSelected: function(event, node) {
  selectedController = node.text;
},
onNodeUnselected: function (event, node) {
  selectedController = null;
}
});

$('#processorstree').treeview({
data: $("#processorsGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
onNodeSelected: function(event, node) {
  selectedProcessor = node.text;
},
onNodeUnselected: function (event, node) {
  selectedProcessor = null;
}
});

$('#datasubjectstree').treeview({
data: $("#dsGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
onNodeSelected: function(event, node) {
  selectedDataSubject = node.text;
},
onNodeUnselected: function (event, node) {
  selectedDataSubject = null;
}
});

$('#recipientstree').treeview({
data: $("#recipientsGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
onNodeSelected: function(event, node) {
  selectedRecipient = node.text;
},
onNodeUnselected: function (event, node) {
  selectedRecipient = null;
}
});

$('#attrtree').treeview({
data: $("#attributeGT").val(),
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
//expandIcon: 'glyphicon glyphicon-chevron-right',
//collapseIcon: 'glyphicon glyphicon-chevron-down'
});

var selectedAction = $('#action-value').text();
var selectedData = $('#data-value').text();
var selectedPurpose = $('#purpose-value').text();
var selectedLegalBasis = $('#legalbasis-value').text();
var selectedController = $('#controller-value').text();
var selectedProcessor = $('#processor-value').text();
var selectedDataSubject = $('#datasubject-value').text();
var selectedSender = null;
var selectedRecipient = $('#recipient-value').text();

$('#save-added-action').click(function() {
    $('#modal-container-add-action').modal('hide');
    if(selectedAction != null){
        $('#action-value').text(selectedAction)
    }
 });

 $('#action-delete').click(function() {
     selectedAction = null;
     $('#action-value').text("");
  });

$('#save-added-data').click(function() {
    $('#modal-container-add-data').modal('hide');
    if(selectedData != null){
        $('#data-value').text(selectedData)
    }
 });

 $('#data-delete').click(function() {
     selectedData = null;
     $('#data-value').text("");
  });

$('#save-added-purpose').click(function() {
    $('#modal-container-add-purpose').modal('hide');
    if(selectedPurpose != null){
        $('#purpose-value').text(selectedPurpose)
    }
 });

 $('#purpose-delete').click(function() {
     selectedPurpose = null;
     $('#purpose-value').text("");
  });

$('#save-added-legalbasis').click(function() {
    $('#modal-container-add-legalbasis').modal('hide');
    if(selectedLegalBasis != null){
        $('#legalbasis-value').text(selectedLegalBasis)
    }
 });

 $('#legalbasis-delete').click(function() {
     selectedLegalBasis = null;
     $('#legalbasis-value').text("");
  });

$('#save-added-controller').click(function() {
    $('#modal-container-add-controller').modal('hide');
    if(selectedController != null){
        $('#controller-value').text(selectedController)
    }
 });

 $('#controller-delete').click(function() {
     selectedController = null;
     $('#controller-value').text("");
  });


$('#save-added-processor').click(function() {
    $('#modal-container-add-processor').modal('hide');
    if(selectedProcessor != null){
        $('#processor-value').text(selectedProcessor)
    }
 });

 $('#processor-delete').click(function() {
     selectedProcessor = null;
     $('#processor-value').text("");
  });

 $('#save-added-datasubject').click(function() {
     $('#modal-container-add-datasubject').modal('hide');
     if(selectedDataSubject != null){
         $('#datasubject-value').text(selectedDataSubject)
     }
  });

  $('#datasubject-delete').click(function() {
      selectedDataSubject = null;
      $('#datasubject-value').text("");
   });

 $('#save-added-recipient').click(function() {
     $('#modal-container-add-recipient').modal('hide');
     if(selectedRecipient != null){
         $('#recipient-value').text(selectedRecipient)
     }
  });

  $('#recipient-delete').click(function() {
      selectedRecipient = null;
      $('#recipient-value').text("");
   });

$("#erase-request-btn").click(function() {
    $('#modal-container-erase-rule').modal('hide');
    selectedAction = null;
    $('#action-value').text("");
    selectedData = null;
    $('#data-value').text("");
    selectedPurpose = null;
    $('#purpose-value').text("");
    selectedLegalBasis = null;
    $('#legalbasis-value').text("");
    selectedController = null;
    $('#controller-value').text("");
    selectedProcessor = null;
    $('#processor-value').text("");
    selectedDataSubject = null;
    $('#datasubject-value').text("");
    selectedRecipient = null;
    $('#recipient-value').text("");
});

//document.getElementById("checkNewRequestBtn").onclick = function() {
//    $('#requestSwitch').val(true);
//    if(selectedAction && selectedAction != ""){
//        $('#selectedAction').val(selectedAction);
//    }
//    if(selectedData && selectedData != ""){
//        $('#selectedData').val(selectedData);
//    }
//    if(selectedPurpose && selectedPurpose != ""){
//        $('#selectedPurpose').val(selectedPurpose);
//    }
//    if(selectedLegalBasis && selectedLegalBasis != ""){
//        $('#selectedLegalBasis').val(selectedLegalBasis);
//    }
//    if(selectedController && selectedController != ""){
//        $('#selectedController').val(selectedController);
//    }
//    if(selectedProcessor && selectedProcessor != ""){
//        $('#selectedProcessor').val(selectedProcessor);
//    }
//    if(selectedDataSubject && selectedDataSubject != ""){
//        $('#selectedDataSubject').val(selectedDataSubject);
//    }
//    if(selectedRecipient && selectedRecipient != ""){
//        $('#selectedRecipient').val(selectedRecipient);
//    }
//    document.getElementById("checkSavedRequestsForm").submit();
//}

document.getElementById("checkSavedRequestsBtn").onclick = function() {
//    $('#requestSwitch').val(false);

//    if(selectedAction && selectedAction != ""){
//        $('#selectedAction').val(selectedAction);
//    }
//    if(selectedData && selectedData != ""){
//        $('#selectedData').val(selectedData);
//    }
//    if(selectedPurpose && selectedPurpose != ""){
//        $('#selectedPurpose').val(selectedPurpose);
//    }
//    if(selectedLegalBasis && selectedLegalBasis != ""){
//        $('#selectedLegalBasis').val(selectedLegalBasis);
//    }
//    if(selectedController && selectedController != ""){
//        $('#selectedController').val(selectedController);
//    }
//    if(selectedProcessor && selectedProcessor != ""){
//        $('#selectedProcessor').val(selectedProcessor);
//    }
//    if(selectedDataSubject && selectedDataSubject != ""){
//        $('#selectedDataSubject').val(selectedDataSubject);
//    }
//    if(selectedRecipient && selectedRecipient != ""){
//        $('#selectedRecipient').val(selectedRecipient);
//    }
    document.getElementById("checkSavedRequestsForm").submit();
}

});