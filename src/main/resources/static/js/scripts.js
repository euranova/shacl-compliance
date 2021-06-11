$(function() {

var ruletree = [
  {
    text: "save:Rule",
      nodes: [
      {
        text: "save:Permission",
      },
      {
        text: "save:Prohibition",
      },
      {
        text: "save:Obligation",
      },
      {
        text: "save:Dispensation",
      }
    ]
  },

];


$('#ruletree').treeview({
data: ruletree,
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
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

$('#actiontree').treeview({
data: actiontree,
color: "#428bca",
showBorder: false,
expandIcon: 'fa fa-angle-down fa-fw',
collapseIcon: 'fa fa-angle-up fa-fw',
});

$('#attributetree').treeview({
    data: actiontree,
    color: "#428bca",
    showBorder: false,
    expandIcon: 'fa fa-angle-down fa-fw',
    collapseIcon: 'fa fa-angle-up fa-fw',
});


});
