document.getElementById("checkSavedRequestsBtn").onclick = function() {
    document.getElementById("checkSavedRequestsForm").submit();
}


////document.addEventListener("DOMContentLoaded", function(event) {
////    let rules, idxPrivacySegment = 0, approvedRules = [],
////        attrObj = {
////            "rule": "rule",
////            "action": "action",
////            "personalInformationType": "data",
////            "purpose": "purpose"
////        }, attributeSave = {
////            "rule": "orcp:Rule",
////            "action": "dpv:Processing",
////            "data": "dpv:PersonalDataCategory",
////            "purpose": "dpv:Purpose"
////        },
////        manualRule= [{
////            "rule":"",
////            "action":[],
////            "personalInformationType":[],
////            "purpose":[]
////        }],selectedAttributeNode,selectedAttributeValueNode,selectedAttributeValue,
////        idx = 0,ruleIdx,segmentStatus=[[]],selectedAttributeName;
////
//
//
////    getResponse().then(response => {
//////        initDocumentTitle(response);
//////        initSegmentContainer(response, idxPrivacySegment);
//////        rules = initRulesContainer(response.rules, idxPrivacySegment);
////
//////        document.getElementById('next_button').addEventListener(
//////            'click', // we want to listen for a click
//////            function (e) { // the e here is the event itself
//////                idxPrivacySegment = nextItem(idxPrivacySegment, response);
//////                initSegmentContainer(response, idxPrivacySegment);
//////                updateApprovedRulesContainer(approvedRules, idxPrivacySegment);
//////                rules = initRulesContainer(rules, idxPrivacySegment);
//////
//////            });
//////
//////        document.getElementById('prev_button').addEventListener(
//////            'click', // we want to listen for a click
//////            function (e) { // the e here is the event itself
//////                idxPrivacySegment = prevItem(idxPrivacySegment, response);
//////                initSegmentContainer(response, idxPrivacySegment);
//////                updateApprovedRulesContainer(approvedRules, idxPrivacySegment);
//////                rules = initRulesContainer(rules, idxPrivacySegment);
//////
//////            });
////        document.addEventListener(
////            'click', // we want to listen for a click
////            function (e) { // the e here is the event itself
////                if (e.target && e.target.id.includes('delete-button-rule')) {
////                    let rIdx = e.target.id.split('-')[3];
////                    initRulesContainer(deleteRule(rules, idxPrivacySegment, rIdx), idxPrivacySegment);
////                }
////                if (e.target && e.target.id.includes('approve-button-rule')) {
////                    let rIdx = e.target.id.split('-')[3];
////                    rules = approveExtractedRule(rules, approvedRules, idxPrivacySegment, rIdx);
////                    updateApprovedRulesContainer(approvedRules, idxPrivacySegment);
////                    initRulesContainer(rules, idxPrivacySegment);
////                }
////
////                if (e.target && e.target.id.includes('delete-button-approved-rule')) {
////                    let rIdx = e.target.id.split('-')[4];
////                    updateApprovedRulesContainer(deleteRule(approvedRules, idxPrivacySegment, rIdx), idxPrivacySegment);
////                }
////                if (e.target && e.target.id.includes('unconfirm-button-rule')) {
////                    let rIdx = e.target.id.split('-')[3];
////                    dataCO = unconfirmRule(approvedRules, rules, idxPrivacySegment, rIdx);
////                    approvedRules = dataCO[0];
////                    rules = dataCO[1];
////                    updateApprovedRulesContainer(approvedRules, idxPrivacySegment);
////                    initRulesContainer(rules, idxPrivacySegment);
////                }
////                if (e.target && e.target.id.includes('delete-button-approved-rule')) {
////                    let rIdx = e.target.id.split('-')[4];
////                    updateApprovedRulesContainer(deleteRule(approvedRules, idxPrivacySegment, rIdx), idxPrivacySegment);
////                }
////                if (e.target && e.target.id.includes('delete-button-approved-rule')) {
////                    let rIdx = e.target.id.split('-')[4];
////                    updateApprovedRulesContainer(deleteRule(approvedRules, idxPrivacySegment, rIdx), idxPrivacySegment);
////                }
////                if (e.target && e.target.id.includes('add-rule')) {
////                    let rIdx = 0;
////                    updateManualRulesContainer(manualRule, idxPrivacySegment, rIdx);
////                }
////                /* Start Manual attributes*/
////                /* ---------------------------------------------------------- */
////                if (e.target && e.target.id.includes('add-attribute-manual')) {
////                    $('.attributetree').treeview({
////                        data: [
////                            {
////                                "text": "rule",
////                            },
////                            {
////                                "text": "action",
////                            },
////                            {
////                                "text": "data",
////                            },
////                            {
////                                "text": "purpose",
////                            },
////                        ],
////                        color: "#428bca",
////                        showBorder: false,
////                        expandIcon: 'fa fa-angle-down fa-fw',
////                        collapseIcon: 'fa fa-angle-up fa-fw',
////                    }).on('nodeSelected', function(e, node){
////                        selectedAttributeNode = node;
////                    });
////                }
////                if (e.target && e.target.id.includes('add-value-attribute')){
////                    getTreeResponse(attributeSave[selectedAttributeNode.text]).then(response => {
////                        $('.rulevaluetree').treeview({
////                            data: response,
////                            color: "#428bca",
////                            showBorder: false,
////                            expandIcon: 'fa fa-angle-down fa-fw',
////                            collapseIcon: 'fa fa-angle-up fa-fw',
////                        }).on('nodeSelected', function(e, node){
////                            selectedAttributeValueNode = node;
////                        });
////                    });
////                    addAttributeModals(selectedAttributeNode,selectedAttributeValueNode,idxPrivacySegment,manualRule);
////                }
////                if (e.target && e.target.id.includes('add-manual-value')) {
////                    idx=0;
////                    manualRule = addAttributeRow(selectedAttributeNode,selectedAttributeValueNode,idxPrivacySegment,manualRule,idx);
////
////                }
////                if (e.target && e.target.id.includes('add-value-attribute-manual')) {
////                    let selectedAttributeName= e.target.id.split('-')[4];
////                    getTreeResponse(attributeSave[selectedAttributeName]).then(response => {
////                        $('.rulevaluetree').treeview({
////                            data: response,
////                            color: "#428bca",
////                            showBorder: false,
////                            expandIcon: 'fa fa-angle-down fa-fw',
////                            collapseIcon: 'fa fa-angle-up fa-fw',
////                        }).on('nodeSelected', function(e, node){
////                            selectedAttributeValueNode = node;
////                        });
////                    });
////                }
////                if (e.target && e.target.id.includes('add-attr-val')) {
////                    let selectedAttributeName= e.target.id.split('-')[3];
////                    idx = idx +1;
////                    manualRule = addValueManualRule(selectedAttributeName,selectedAttributeValueNode.text,idxPrivacySegment,manualRule,idx);
////                }
////                if (e.target && e.target.id.includes('btn-modal-container-edit')) {
////                    let selectedAttributeName= e.target.id.split('-')[4];
////                    if (selectedAttributeName.includes("rule")){
////                        selectedAttributeValue= document.getElementById(e.target.id).previousElementSibling.textContent;
////
////                    }
////                    else  {
////                        selectedAttributeValue= document.getElementById(e.target.id).previousElementSibling.previousElementSibling.textContent;
////
////                    }
////                    getTreeResponse(attributeSave[selectedAttributeName]).then(response => {
////                        $('.rulevaluetree').treeview({
////                            data: response,
////                            color: "#428bca",
////                            showBorder: false,
////                            expandIcon: 'fa fa-angle-down fa-fw',
////                            collapseIcon: 'fa fa-angle-up fa-fw',
////                        }).on('nodeSelected', function(e, node){
////                            selectedAttributeValueNode = node;
////                        });
////                    });
////                }
////                if (e.target && e.target.id.includes('edit-value')) {
////                    let selectedAttributeName= e.target.id.split('-')[2];
////                    manualRule = updateManualRule(selectedAttributeName,selectedAttributeValue,selectedAttributeValueNode.text,idxPrivacySegment,manualRule,idx);
////                }
////                if (e.target && e.target.id.includes('delete-all-attribute')) {
////                    let selectedAttributeName= e.target.id.split('-')[3];
////                    manualRule = deleteAllAttribute(selectedAttributeName,selectedAttributeValue,idxPrivacySegment,manualRule);
////                }
////                if (e.target && e.target.id.includes('btn-modal-container-delete')){
////                    let selectedAttributeName= e.target.id.split('-')[4];
////                    selectedAttributeValue= document.getElementById(e.target.id).previousElementSibling.textContent;
////                    idx = e.target.id.split('-')[5];
////                }
////                if (e.target && e.target.id.includes('delete-from-attribute')) {
////                    let selectedAttributeName = e.target.id.split('-')[3];
////                    manualRule = deleteFromAttribute(selectedAttributeName, selectedAttributeValue, idxPrivacySegment, manualRule, idx);
////                }
////                /* End Manual attributes*/
////                /* ---------------------------------------------------------- */
////                /* Start Extracted attributes*/
////                /* ---------------------------------------------------------- */
////                if (e.target && e.target.id.includes('add-attribute-extracted')) {
////                    ruleIdx = e.target.id.split('-')[3];
////                    $('.attributetree').treeview({
////                        data: [
////                            {
////                                "text": "rule",
////                            },
////                            {
////                                "text": "action",
////                            },
////                            {
////                                "text": "data",
////                            },
////                            {
////                                "text": "purpose",
////                            },
////                        ],
////                        color: "#428bca",
////                        showBorder: false,
////                        expandIcon: 'fa fa-angle-down fa-fw',
////                        collapseIcon: 'fa fa-angle-up fa-fw',
////                    }).on('nodeSelected', function(e, node){
////                        selectedAttributeNode = node;
////                    });
////                }
////                if (e.target && e.target.id.includes('add-value-attribute-extracted')){
////                    getTreeResponse(attributeSave[selectedAttributeNode.text]).then(response => {
////                        $('.rulevaluetree').treeview({
////                            data: response,
////                            color: "#428bca",
////                            showBorder: false,
////                            expandIcon: 'fa fa-angle-down fa-fw',
////                            collapseIcon: 'fa fa-angle-up fa-fw',
////                        }).on('nodeSelected', function(e, node){
////                            selectedAttributeValueNode = node;
////                        });
////                    });
////                }
////                if (e.target && e.target.id.includes('add-extracted-value')) {
////                    if (selectedAttributeNode.text.includes("data")){
////                        if (!rules[idxPrivacySegment][ruleIdx]["personalInformationType"].length){
////                            idx=0;}
////                        else{
////                            idx = rules[idxPrivacySegment][ruleIdx]["personalInformationType"].length -1;
////                        }
////                    }
////                    else if (!rules[idxPrivacySegment][ruleIdx][selectedAttributeNode.text].length){
////                        idx=0;
////                    }
////                    else {
////                        idx = rules[idxPrivacySegment][ruleIdx][selectedAttributeNode.text].length -1;
////
////                    }
////                    rules = addExtractedAttributeRow(selectedAttributeNode,selectedAttributeValueNode,ruleIdx,idxPrivacySegment,rules,idx);
////                }
////                if (e.target && e.target.id.includes('delete-extracted-all')) {
////                    let selectedAttributeName= e.target.id.split('-')[3];
////                    let ruleIdx = e.target.id.split('-')[4];
////                    rules = deleteExtractedAllAttribute(selectedAttributeName,selectedAttributeValue,ruleIdx,idxPrivacySegment,rules);
////                }
////                if (e.target && e.target.id.includes('delete-from-extracted-attribute')) {
////                    let selectedAttributeName = e.target.id.split('-')[4];
////                    ruleIdx = e.target.id.split('-')[5];
////                    idx = e.target.id.split('-')[6];
////                    selectedAttributeValue= document.getElementById(e.target.id).previousElementSibling.textContent;
////                    rules = deleteFromExtractedAttribute(selectedAttributeName, selectedAttributeValue, ruleIdx,idxPrivacySegment, rules, idx);
////                }
////                if (e.target && e.target.id.includes('btn-modal-container-extracted-edit')) {
////                    let selectedAttributeName= e.target.id.split('-')[5];
////                    ruleIdx = e.target.id.split('-')[6];
////                    idx = e.target.id.split('-')[7];
////
////                    if (selectedAttributeName.includes("rule")){
////                        selectedAttributeValue= document.getElementById(e.target.id).previousElementSibling.textContent;
////
////                    }
////                    else  {
////                        selectedAttributeValue= document.getElementById(e.target.id).previousElementSibling.previousElementSibling.textContent;
////
////                    }
////                    getTreeResponse(attributeSave[selectedAttributeName]).then(response => {
////                        $('.rulevaluetree').treeview({
////                            data: response,
////                            color: "#428bca",
////                            showBorder: false,
////                            expandIcon: 'fa fa-angle-down fa-fw',
////                            collapseIcon: 'fa fa-angle-up fa-fw',
////                        }).on('nodeSelected', function(e, node){
////                            selectedAttributeValueNode = node;
////                        });
////                    });
////                }
////                if (e.target && e.target.id.includes('edit-extracted-value')) {
////                    let selectedAttributeName= e.target.id.split('-')[3];
////                    rules = updateExtractedRule(selectedAttributeName,selectedAttributeValue,selectedAttributeValueNode.text,ruleIdx,idxPrivacySegment,rules,idx);
////                }
////                if (e.target && e.target.id.includes('add-value-extracted-attribute')) {
////                    selectedAttributeName= e.target.id.split('-')[4];
////                    ruleIdx = e.target.id.split('-')[5];
////                    getTreeResponse(attributeSave[selectedAttributeName]).then(response => {
////                        $('.rulevaluetree').treeview({
////                            data: response,
////                            color: "#428bca",
////                            showBorder: false,
////                            expandIcon: 'fa fa-angle-down fa-fw',
////                            collapseIcon: 'fa fa-angle-up fa-fw',
////                        }).on('nodeSelected', function(e, node){
////                            selectedAttributeValueNode = node;
////                        });
////                    });
////                }
////                if (e.target && e.target.id.includes('add-value-to-attribute-extracted')) {
////                    if (selectedAttributeName.includes("data")){
////                        if (!rules[idxPrivacySegment][ruleIdx]["personalInformationType"].length){
////                            idx=0;}
////                        else{
////                            idx = rules[idxPrivacySegment][ruleIdx]["personalInformationType"].length;
////                        }
////                    }
////                    else if (!rules[idxPrivacySegment][ruleIdx][selectedAttributeName].length){
////                        idx=0;
////                    }
////                    else {
////                        idx = rules[idxPrivacySegment][ruleIdx][selectedAttributeName].length;
////
////                    }
////                    rules = addValueExtractedRule(selectedAttributeName,selectedAttributeValueNode.text,ruleIdx,idxPrivacySegment,rules,idx);
////                }
////                /* End Extracted attributes*/
////                /* ---------------------------------------------------------- */
////                if (e.target && e.target.id.includes('approve-manual-button-rule')) {
////                    let rIdx = e.target.id.split('-')[4];
////                    approvedRules = approveManualRule(manualRule,approvedRules,idxPrivacySegment,rIdx);
////                    updateApprovedRulesContainer(approvedRules, idxPrivacySegment);
////                }
////
////                if (e.target && e.target.id.includes('delete-manual-button-rule')) {
////                    let rIdx = e.target.id.split('-')[4];
////                    deleteManualRule();
////                }
////
////                if (e.target && e.target.id.includes('approve-segment')) {
////                    alert("This segment will be ignored. It has no SAVE rules.");
////                }
////                if (e.target && e.target.id.includes('flag-segment')) {
////                    alert("This segment will be flagged. You can come back later and review it.");
////
////                }
////                if (e.target && e.target.id.includes('ignore-segment')) {
////                    alert("This segment will be ignored. It has no SAVE rules.");
////                    rules[idxPrivacySegment] = [];
////                    rules = initRulesContainer(rules, idxPrivacySegment);
////                    approvedRules[idxPrivacySegment] = [];
////                    updateApprovedRulesContainer(approvedRules,idxPrivacySegment);
////
////                }
////
////            }
////        );
////    });
//});
//
//function deleteExtractedAllAttribute(selectedAttributeName,selectedAttributeValue,ruleIdx,idxPrivacySegment,rules){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//    rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeName]] = [];
//    let elements = document.getElementsByClassName(`tr-${selectedAttributeName}`);
//    Array.from(elements).forEach((el) => {
//        if (el.id.includes(`tr-${selectedAttributeName}-${ruleIdx}`)){
//            // Do stuff here
//            el.remove();
//        }
//    });
//    return rules;
//
//}
//
//function addExtractedAttributeRow(selectedAttributeNode,selectedAttributeValueNode,ruleIdx,idxPrivacySegment,rules,idx){
//    let html;
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//    if(!rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]].length){
//        html = `<tr class="tr-${selectedAttributeNode.text}" id="tr-${selectedAttributeNode.text}-${ruleIdx}-${idx}">
//                        <td align="right"><span>${selectedAttributeNode.text}</span>
//               <a href="#modal-container-delete-${selectedAttributeNode.text}-attribute" id="delete-extracted-all-${selectedAttributeNode.text}-${ruleIdx}-attribute">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event"
//                                fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd"
//                                    d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z" />
//                            </svg>
//                        </a>
//                        <a data-toggle="modal" href="#modal-container-add-${selectedAttributeNode.text}-attribute" id="btn-modal-container-extracted-add-${selectedAttributeNode.text}-${ruleIdx}-type">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-node-plus svg-event"
//                                fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd"
//                                    d="M11 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8zM6.025 7.5a5 5 0 1 1 0 1H4A1.5 1.5 0 0 1 2.5 10h-1A1.5 1.5 0 0 1 0 8.5v-1A1.5 1.5 0 0 1 1.5 6h1A1.5 1.5 0 0 1 4 7.5h2.025zM11 5a.5.5 0 0 1 .5.5v2h2a.5.5 0 0 1 0 1h-2v2a.5.5 0 0 1-1 0v-2h-2a.5.5 0 0 1 0-1h2v-2A.5.5 0 0 1 11 5zM1.5 7a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1z" />
//                            </svg>
//                        </a>
//                    </td>
//                    <td align="right"><span>${selectedAttributeValueNode.text}</span>
//               <a href="#modal-container-delete-${selectedAttributeNode.text}-type" id="delete-from-extracted-attribute-${selectedAttributeNode.text}-${ruleIdx}-${idx}">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event"
//                                fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd"
//                                    d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z" />
//                            </svg>
//                        </a>
//                     <a data-toggle="modal" href="#modal-container-edit-${selectedAttributeNode.text}-${ruleIdx}-${idx}-type" id="btn-modal-container-extracted-edit-${selectedAttributeNode.text}-${ruleIdx}-type">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16"
//                                class="bi bi-pencil-square svg-event" fill="currentColor"
//                                xmlns="http://www.w3.org/2000/svg">
//                                <path
//                                    d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z" />
//                                <path fill-rule="evenodd"
//                                    d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z" />
//                            </svg>
//                        </a>
//            </td></tr>`
//        document.getElementById(`rule-ct-body-${ruleIdx}`).insertAdjacentHTML('beforeend', html);
//    }
//    else {
//        html = `<tr class="tr-${selectedAttributeNode.text}" id="tr-${selectedAttributeNode.text}-${ruleIdx}-${idx+1}">
//                        <td></td>
//                    <td align="right"><span>${selectedAttributeValueNode.text}</span>
//                        <a href="#modal-container-delete-${selectedAttributeNode.text}-${ruleIdx}-${rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]].length}-type" id="delete-from-extracted-attribute-${selectedAttributeNode.text}-${ruleIdx}-${rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]].length}">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd" d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z"></path>
//                            </svg>
//                        </a>
//            <a data-toggle="modal" href="#modal-container-edit-${selectedAttributeNode.text}-${ruleIdx}-${rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]].length}-type" id="btn-modal-container-extracted-edit-${selectedAttributeNode.text}-${ruleIdx}-type"-${rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]].length}">
//                                       <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-pencil-square svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                           <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"></path>
//                                           <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z"></path>
//                                       </svg>
//                    </a>
//                </td></tr>`;
//        document.getElementById(`tr-${selectedAttributeNode.text}-${ruleIdx}-${idx}`).insertAdjacentHTML('afterend', html);
//    }
//    return addExtractedRule(selectedAttributeNode,selectedAttributeValueNode,ruleIdx,idxPrivacySegment,rules);
//}
//
//function addValueExtractedRule(selectedAttributeName,selectedAttributeValueNodeText,ruleIdx,idxPrivacySegment,rules,idx){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//    if (!rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeName]].includes(selectedAttributeValueNodeText)){
//        rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeName]].push(selectedAttributeValueNodeText);
//        let html=`<tr class="tr-${selectedAttributeName}" id="tr-${selectedAttributeName}-${idx}">
//                        <td></td>
//                    <td align="right"><span>${selectedAttributeValueNodeText}</span>
//                        <a data-toggle="modal" href="#modal-container-delete-${selectedAttributeName}-type" id ="btn-modal-container-delete-${selectedAttributeName}-${idx}-type">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd" d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z"></path>
//                            </svg>
//                        </a>
//            <a data-toggle="modal" href="#modal-container-edit-${selectedAttributeName}-type" id ="btn-modal-container-edit-${selectedAttributeName}-${idx}-type">
//                                       <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-pencil-square svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                           <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"></path>
//                                           <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z"></path>
//                                       </svg>
//                    </a>
//                </td></tr>`
//        document.getElementById(`tr-${selectedAttributeName}-${ruleIdx}-${idx -1}`).insertAdjacentHTML('afterend',html);
//    }
//    return rules;
//}
//
//function updateExtractedRule(selectedAttributeName,selectedAttributeValue,selectedAttributeValueNodeText,ruleIdx,idxPrivacySegment,rules,idx){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//
//    if (selectedAttributeName.includes("rule") ){
//        $("#rule-ct-body-" + ruleIdx + " td span:contains('"+selectedAttributeValue +"')").html(selectedAttributeValueNodeText);
//        rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeName]] = selectedAttributeValueNodeText;
//    }
//    else
//    {
//        let valueIdx = rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeName]].indexOf(selectedAttributeValue);
//        rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeName]][valueIdx] = selectedAttributeValueNodeText;
//        $("#rule-ct-body-" + ruleIdx + " td span:contains('"+selectedAttributeValue +"')").html(selectedAttributeValueNodeText);
//    }
//    return rules;
//}
//
//function deleteFromExtractedAttribute(selectedAttributeName, selectedAttributeValue, ruleIdx,idxPrivacySegment, rules, idx){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//    let valueIdx = rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeName]].indexOf(selectedAttributeValue);
//    rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeName]].splice(valueIdx, 1)
//
//    if (idx > 0) {
//        document.getElementById(`tr-${selectedAttributeName}-${ruleIdx}-${idx}`).remove();
//    }
//    else if(idx < 1 && document.getElementById(`tr-${selectedAttributeName}-${ruleIdx}-${idx+1}`) == null) {
//        alert(`A SAVE rule must have at least 1 value for ${selectedAttributeName}`);
//    }
//    else {
//        document.getElementById(`tr-${selectedAttributeName}-${ruleIdx}-${idx}`).remove();
//    }
//    return rules;
//}
//
//function addExtractedRule(selectedAttributeNode,selectedAttributeValueNode,ruleIdx,idxPrivacySegment,rules){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//    if (typeof rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]] =="undefined" && selectedAttributeNode.text.includes("rule") ){
//        rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]] = selectedAttributeValueNode.text
//    }
//    else if (Array.isArray(rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]]) &&  !rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]].length)
//    {
//        rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]] = [selectedAttributeValueNode.text];
//    }
//    else {
//        rules[idxPrivacySegment][ruleIdx][attrObjInv[selectedAttributeNode.text]].push(selectedAttributeValueNode.text);
//    }
//    return rules;
//}
//
//function deleteManualRule(){
//    document.getElementById('manual-rule-ct').innerHTML = `<div id="manual-rule-ct"></div>`;
//
//}
//
//function approveManualRule(manualRule,approvedRulesList,idxPrivacySegment,rIdx){
//    if (typeof approvedRulesList[idxPrivacySegment] == "undefined") {
//        approvedRulesList[idxPrivacySegment] = [manualRule[idxPrivacySegment]];
//    } else {
//        approvedRulesList[idxPrivacySegment].push(manualRule[idxPrivacySegment]);
//    }
//    document.getElementById('manual-rule-ct').innerHTML = `<div id="manual-rule-ct"></div>`;
//    return approvedRulesList;
//}
//
//function addAttributeModals(selectedAttributeNode,selectedAttributeValueNode,idxPrivacySegment,manualRule){
//    let html;
//    html=`<div aria-hidden="true" aria-labelledby="labelEdit${selectedAttributeNode.text}Type" class="modal fade"
//                         id="modal-container-edit-${selectedAttributeNode.text}-type" role="dialog">
//                        <div class="modal-dialog" role="document">
//                            <div class="modal-content">
//                                <div class="modal-header">
//                                    <h5 class="modal-title" id="labelEdit${selectedAttributeNode.text}Type">
//                                        Edit ${selectedAttributeNode.text} value
//                                    </h5>
//                                    <button class="close" data-dismiss="modal" type="button">
//                                        <span aria-hidden="true">×</span>
//                                    </button>
//                                </div>
//                                <div style="max-height: calc(100vh - 210px); overflow-y: auto;" class="modal-body">
//                                    <div class="rulevaluetree"></div>
//                                </div>
//                                <div class="modal-footer">
//
//                                    <button class="btn btn-primary" type="button"  data-dismiss="modal" id="edit-value-${selectedAttributeNode.text}">
//                                        Edit
//                                    </button>
//                                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                                        Close
//                                    </button>
//                                </div>
//                            </div>
//
//                        </div>
//
//                    </div>
//</div>`;
//    if (!selectedAttributeNode.text.includes("rule")){
//        html +=`<div aria-hidden="true" aria-labelledby="labelDelete${selectedAttributeNode.text}Type" class="modal fade"
//                         id="modal-container-delete-${selectedAttributeNode.text}-type" role="dialog">
//                        <div class="modal-dialog" role="document">
//                            <div class="modal-content">
//                                <div class="modal-header">
//                                    <h5 class="modal-title" id="labelDelete${selectedAttributeNode.text}Type">
//                                        Delete ${selectedAttributeNode.text} value
//                                    </h5>
//                                    <button class="close" data-dismiss="modal" type="button">
//                                        <span aria-hidden="true">×</span>
//                                    </button>
//                                </div>
//                                <div class="modal-body">
//                                    Do you really want to delete selected value from ${selectedAttributeNode.text} ?
//                                </div>
//                                <div class="modal-footer">
//
//                                    <button class="btn btn-primary" type="button" data-dismiss="modal"  id="delete-from-attribute-${selectedAttributeNode.text}-value">
//                                        Delete
//                                    </button>
//                                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                                        Close
//                                    </button>
//                                </div>
//                            </div>
//
//                        </div>
//
//                    </div>
//</div>
//<div aria-hidden="true" aria-labelledby="labelDelete${selectedAttributeNode.text}Attribute" class="modal fade"
//                         id="modal-container-delete-${selectedAttributeNode.text}-attribute" role="dialog">
//                        <div class="modal-dialog" role="document">
//                            <div class="modal-content">
//                                <div class="modal-header">
//                                    <h5 class="modal-title" class="attributeDelete${selectedAttributeNode.text}Type">
//                                        Delete ${selectedAttributeNode.text} value
//                                    </h5>
//                                    <button class="close" data-dismiss="modal" type="button">
//                                        <span aria-hidden="true">×</span>
//                                    </button>
//                                </div>
//                                <div class="modal-body">
//                                    Do you really want to delete ${selectedAttributeNode.text} ?
//                                </div>
//                                <div class="modal-footer">
//
//                                    <button class="btn btn-primary" type="button" id="delete-all-attribute-${selectedAttributeNode.text}-value" data-dismiss="modal">
//                                        Delete
//                                    </button>
//                                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                                        Close
//                                    </button>
//                                </div>
//                            </div>
//
//                        </div>
//
//                    </div>
//</div>
//<div aria-hidden="true" aria-labelledby="labelAdd${selectedAttributeNode.text}Attribute" class="modal fade"
//                         id="modal-container-add-${selectedAttributeNode.text}-attribute" role="dialog">
//                        <div class="modal-dialog" role="document">
//                            <div class="modal-content">
//                                <div class="modal-header">
//                                    <h5 class="modal-title" class="attributeAdd${selectedAttributeNode.text}Type">
//                                        Add ${selectedAttributeNode.text} value
//                                    </h5>
//                                    <button class="close" data-dismiss="modal" type="button">
//                                        <span aria-hidden="true">×</span>
//                                    </button>
//                                </div>
//                                <div style="max-height: calc(100vh - 210px); overflow-y: auto;" class="modal-body">
//                                <div class="rulevaluetree"></div>
//                                </div>
//                                <div class="modal-footer">
//
//                                    <button class="btn btn-primary" data-dismiss="modal" type="button" id="add-attr-val-${selectedAttributeNode.text}-value">
//                                        Add
//                                    </button>
//                                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                                        Close
//                                    </button>
//                                </div>
//                            </div>
//
//                        </div>
//
//                    </div>
//</div>`;
//    }
//    document.getElementById('manual-rule-ct').insertAdjacentHTML('beforeEnd', html);
//}
//
//function addManualRule(selectedAttributeNode,selectedAttributeValueNode,idxPrivacySegment,manualRule){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//
//    if (typeof  manualRule[idxPrivacySegment]=="undefined"){
//        manualRule[idxPrivacySegment] = {
//            "rule":"",
//            "action":[],
//            "personalInformationType":[],
//            "purpose":[]
//        };
//    }
//    else {
//        if (typeof  manualRule[idxPrivacySegment][attrObjInv[selectedAttributeNode.text]] =="undefined" && selectedAttributeNode.text.includes("rule") ){
//            manualRule[idxPrivacySegment][attrObjInv[selectedAttributeNode.text]] = selectedAttributeValueNode.text
//        }
//        else if (typeof  manualRule[idxPrivacySegment][attrObjInv[selectedAttributeNode.text]] =="undefined" && !selectedAttributeNode.text.includes("rule")  )
//        {
//            manualRule[idxPrivacySegment][attrObjInv[selectedAttributeNode.text]] = [selectedAttributeValueNode.text];
//        }
//        else if (typeof  manualRule[idxPrivacySegment][attrObjInv[selectedAttributeNode.text]] !=="undefined" && selectedAttributeNode.text.includes("rule")) {
//            manualRule[idxPrivacySegment][attrObjInv[selectedAttributeNode.text]] = selectedAttributeValueNode.text
//        }
//        else {
//            manualRule[idxPrivacySegment][attrObjInv[selectedAttributeNode.text]].push(selectedAttributeValueNode.text);
//
//        }
//    }
//    return manualRule;
//}
//
//function addAttributeRow(selectedAttributeNode,selectedAttributeValueNode,idxPrivacySegment,manualRule,idx){
//    let html;
//
//    if (selectedAttributeNode.text.includes("rule")){
//        html= `<tr>
//           <td align="right"><span>${selectedAttributeNode.text}</span>
//           </td>
//           <td align="right"><span>${selectedAttributeValueNode.text}</span>
//            <a data-toggle="modal" href="#modal-container-edit-${selectedAttributeNode.text}-type" id ="btn-modal-container-edit-${selectedAttributeNode.text}-type">
//                               <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-pencil-square svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                   <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"></path>
//                                   <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z"></path>
//                               </svg>
//            </a>
//           </td>
//           </tr>`;
//        document.getElementById('manual-rule-tbody').innerHTML = html;
//    } else{
//        html = `<tr class="tr-${selectedAttributeNode.text}" id="tr-${selectedAttributeNode.text}-${idx}">
//                        <td align="right"><span>${selectedAttributeNode.text}</span>
//                        <a data-toggle="modal" href="#modal-container-delete-${selectedAttributeNode.text}-attribute">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd" d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z"></path>
//                            </svg>
//                        </a>
//                        <a data-toggle="modal" href="#modal-container-add-${selectedAttributeNode.text}-attribute" id="add-value-attribute-manual-${selectedAttributeNode.text}">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-node-plus svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd" d="M11 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8zM6.025 7.5a5 5 0 1 1 0 1H4A1.5 1.5 0 0 1 2.5 10h-1A1.5 1.5 0 0 1 0 8.5v-1A1.5 1.5 0 0 1 1.5 6h1A1.5 1.5 0 0 1 4 7.5h2.025zM11 5a.5.5 0 0 1 .5.5v2h2a.5.5 0 0 1 0 1h-2v2a.5.5 0 0 1-1 0v-2h-2a.5.5 0 0 1 0-1h2v-2A.5.5 0 0 1 11 5zM1.5 7a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1z"></path>
//                            </svg>
//                        </a>
//                    </td>
//                    <td align="right"><span>${selectedAttributeValueNode.text}</span>
//                        <a data-toggle="modal" href="#modal-container-delete-${selectedAttributeNode.text}-type" id ="btn-modal-container-delete-${selectedAttributeNode.text}-${idx}-type">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd" d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z"></path>
//                            </svg>
//                        </a>
//            <a data-toggle="modal" href="#modal-container-edit-${selectedAttributeNode.text}-type" id ="btn-modal-container-edit-${selectedAttributeNode.text}-${idx}-type">
//                                       <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-pencil-square svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                           <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"></path>
//                                           <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z"></path>
//                                       </svg>
//                    </a>
//                </td></tr>`
//        document.getElementById('manual-rule-tbody').insertAdjacentHTML('beforeend', html);
//    }
//    return addManualRule(selectedAttributeNode,selectedAttributeValueNode,idxPrivacySegment,manualRule);
//}
//
//function deleteAllAttribute(selectedAttributeName,selectedAttributeValue,idxPrivacySegment,manualRule){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//
//    manualRule[idxPrivacySegment][attrObjInv[selectedAttributeName]] = [];
//    let elements = document.getElementsByClassName(`tr-${selectedAttributeName}`)
//    Array.from(elements).forEach((el) => {
//        // Do stuff here
//        el.remove();
//    });
//
//    return manualRule;
//
//}
//
//function deleteFromAttribute(selectedAttributeName,selectedAttributeValue,idxPrivacySegment,manualRule,idx){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//
//    let valueIdx = manualRule[idxPrivacySegment][attrObjInv[selectedAttributeName]].indexOf(selectedAttributeValue);
//    manualRule[idxPrivacySegment][attrObjInv[selectedAttributeName]].splice(valueIdx, 1)
//
//    if (idx > 0) {
//        document.getElementById(`tr-${selectedAttributeName}-${idx}`).innerHTML = ``;
//    }
//    else  if(idx < 1 && document.getElementById(`tr-${selectedAttributeName}-${idx + 1}`) == null) {
//        alert(`A SAVE rule must have at least 1 value for${selectedAttributeName}`);
//    }
//    else {
//        document.getElementById(`tr-${selectedAttributeName}-${idx}`).innerHTML = ``;
//    }
//    return manualRule;
//}
//
//function updateManualRule(selectedAttributeName,selectedAttributeValue,selectedAttributeValueNodeText,idxPrivacySegment,manualRule,idx){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//    if (selectedAttributeName.includes("rule") ){
//        $("#rule-ct-body td span:contains('"+selectedAttributeValue +"')").html(selectedAttributeValueNodeText);
//        manualRule[idxPrivacySegment][attrObjInv[selectedAttributeName]] = selectedAttributeValueNodeText;
//    }
//    else
//    {
//        let valueIdx = manualRule[idxPrivacySegment][attrObjInv[selectedAttributeName]].indexOf(selectedAttributeValue);
//        manualRule[idxPrivacySegment][attrObjInv[selectedAttributeName]][valueIdx] = selectedAttributeValueNodeText;
//        $("#rule-ct-body td span:contains('"+selectedAttributeValue +"')").html(selectedAttributeValueNodeText);
//
//    }
//    return manualRule;
//}
//
//function addValueManualRule(selectedAttributeName,selectedAttributeValueNodeText,idxPrivacySegment,manualRule,idx){
//    let attrObjInv = {
//        "rule": "rule",
//        "action": "action",
//        "data": "personalInformationType",
//        "purpose": "purpose"
//    };
//    if (!manualRule[idxPrivacySegment][attrObjInv[selectedAttributeName]].includes(selectedAttributeValueNodeText)){
//        manualRule[idxPrivacySegment][attrObjInv[selectedAttributeName]].push(selectedAttributeValueNodeText);
//        let html=`<tr class="tr-${selectedAttributeName}" id="tr-${selectedAttributeName}-${idx}">
//                        <td></td>
//                    <td align="right"><span>${selectedAttributeValueNodeText}</span>
//                        <a data-toggle="modal" href="#modal-container-delete-${selectedAttributeName}-type" id ="btn-modal-container-delete-${selectedAttributeName}-${idx}-type">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd" d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z"></path>
//                            </svg>
//                        </a>
//            <a data-toggle="modal" href="#modal-container-edit-${selectedAttributeName}-type" id ="btn-modal-container-edit-${selectedAttributeName}-${idx}-type">
//                                       <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-pencil-square svg-event" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                           <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"></path>
//                                           <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z"></path>
//                                       </svg>
//                    </a>
//                </td></tr>`
//        document.getElementById(`tr-${selectedAttributeName}-${idx-1}`).insertAdjacentHTML('afterend',html);
//    }
//    return manualRule;
//}
//
//function updateManualRulesContainer(manualRule, idxPrivacySegment, ruleIdx) {
//    let html =
//        `<h4 id="rule-ct-header">
//  manual rule
//  <a href="#modal-container-confirm-manual-rule-${ruleIdx}" data-toggle="modal" type="button"
//      class="btn btn-outline-primary" id="confirm-manual-rule${ruleIdx}">
//      <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-file-earmark-check-fill"
//          fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//          <path fill-rule="evenodd"
//              d="M2 2a2 2 0 0 1 2-2h5.293A1 1 0 0 1 10 .293L13.707 4a1 1 0 0 1 .293.707V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm7.5 1.5v-2l3 3h-2a1 1 0 0 1-1-1zm1.354 4.354a.5.5 0 0 0-.708-.708L7.5 9.793 6.354 8.646a.5.5 0 1 0-.708.708l1.5 1.5a.5.5 0 0 0 .708 0l3-3z">
//          </path>
//      </svg>
//  </a>
//  <a href="#modal-container-delete-manual-rule-${ruleIdx}" data-toggle="modal" type="button"
//      class="btn btn-outline-primary" id="delete-manual-rule-${ruleIdx}">
//      <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event"
//          fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//          <path fill-rule="evenodd"
//              d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z">
//          </path>
//      </svg>
//  </a>
//</h4>
//<div aria-hidden="true" aria-labelledby="labelConfirmRule" class="modal fade"
//id="modal-container-confirm-manual-rule-${ruleIdx}" role="dialog">
//<div class="modal-dialog" role="document">
//  <div class="modal-content">
//      <div class="modal-header">
//          <h5 class="modal-title" id="labelConfirmRule">
//              Approve Rule
//          </h5>
//          <button class="close" data-dismiss="modal" type="button">
//          <span aria-hidden="true">×</span>
//      </button>
//      </div>
//      <div class="modal-body">
//          Do you want to approve Manual Rule ${ruleIdx + 1}
//      </div>
//      <div class="modal-footer">
//
//          <button class="btn btn-primary" type="button" data-dismiss="modal" id="approve-manual-button-rule-${ruleIdx}">
//              Approve
//          </button>
//          <button class="btn btn-secondary" data-dismiss="modal" type="button">
//              Cancel
//          </button>
//      </div>
//  </div>
//  </div>
//  </div>
//
//<div aria-hidden="true" aria-labelledby="labelDeleteRule" class="modal fade"
//   id="modal-container-delete-manual-rule-${ruleIdx}" role="dialog">
//  <div class="modal-dialog" role="document">
//      <div class="modal-content">
//          <div class="modal-header">
//              <h5 class="modal-title" id="labelDeleteRule">
//                  Delete Rule
//              </h5>
//              <button class="close" data-dismiss="modal" type="button">
//                  <span aria-hidden="true">×</span>
//              </button>
//          </div>
//          <div class="modal-body">
//              Do you really want to delete rule ${ruleIdx + 1} ?
//          </div>
//          <div class="modal-footer">
//
//              <button class="btn btn-primary" data-dismiss="modal" type="button" id="delete-manual-button-rule-${ruleIdx}">
//                  Delete
//              </button>
//              <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                  Cancel
//              </button>
//          </div>
//      </div>
//  </div>
//</div>
//<table class="table table-striped table-hover" id="rule-ct-body">
//  <thead align="right">
//      <tr>
//          <th scope="col" align="right"><span>attribute</span>
//              <a data-toggle="modal" href="#modal-container-add-manual-attribute"  id="add-attribute-manual">
//                  <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-node-plus svg-event"
//                      fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                      <path fill-rule="evenodd"
//                          d="M11 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8zM6.025 7.5a5 5 0 1 1 0 1H4A1.5 1.5 0 0 1 2.5 10h-1A1.5 1.5 0 0 1 0 8.5v-1A1.5 1.5 0 0 1 1.5 6h1A1.5 1.5 0 0 1 4 7.5h2.025zM11 5a.5.5 0 0 1 .5.5v2h2a.5.5 0 0 1 0 1h-2v2a.5.5 0 0 1-1 0v-2h-2a.5.5 0 0 1 0-1h2v-2A.5.5 0 0 1 11 5zM1.5 7a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1z" />
//                  </svg>
//              </a>
//          </th>
//          <th scope="col" align="right">values</th>
//      </tr>
//  </thead>
//  <tbody id ="manual-rule-tbody">`
//    html += ` </tbody>
//               </table>
//         <div aria-hidden="true" aria-labelledby="addLabel" class="modal fade" id="modal-container-add-manual-attribute"
//         role="dialog">
//        <div class="modal-dialog" role="document">
//            <div class="modal-content">
//                <div class="modal-header">
//                    <h5 class="modal-title" id="addLabel">
//                        Add Attribute Entry
//                    </h5>
//                    <button class="close" data-dismiss="modal" type="button">
//                        <span aria-hidden="true">×</span>
//                    </button>
//                </div>
//                <div class="modal-body">
//                    <div class="attributetree"></div>
//                </div>
//                <div class="modal-footer">
//
//                    <button href= "#modal-container-add-manual-value" class="btn btn-primary" type="button" id="add-value-attribute" data-toggle="modal" data-dismiss="modal">
//                        Add
//                    </button>
//                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                        Close
//                    </button>
//                </div>
//            </div>
//
//        </div>
//        </div>
//        <div aria-hidden="true" aria-labelledby="addValue" class="modal fade" id="modal-container-add-manual-value"
//         role="dialog">
//        <div class="modal-dialog" role="document">
//            <div class="modal-content">
//                <div class="modal-header">
//                    <h5 class="modal-title" id="addValue">
//                        Add Attribute Value
//                    </h5>
//                    <button class="close" data-dismiss="modal" type="button">
//                        <span aria-hidden="true">×</span>
//                    </button>
//                </div>
//                <div style="max-height: calc(100vh - 210px); overflow-y: auto;" class="modal-body">
//                    <div class="rulevaluetree"></div>
//                </div>
//                <div class="modal-footer">
//
//                    <button class="btn btn-primary" type="button" id="add-manual-value" data-dismiss="modal">
//                        Confirm
//                    </button>
//                    <button href="#modal-container-add-manual-attribute" class="btn btn-secondary" data-toggle="modal" data-dismiss="modal" type="button">
//                        Close
//                    </button>
//                </div>
//            </div>
//
//        </div>
//        </div>`;
//    document.getElementById('manual-rule-ct').innerHTML = html;
//
//}
//
//function unconfirmRule(inputDataC, inputDataO, idxPrivacySegment, rIdx) {
//    if (typeof inputDataO[idxPrivacySegment] == "undefined") {
//        inputDataO[idxPrivacySegment] = inputDataC[idxPrivacySegment][rIdx];
//    } else {
//        inputDataO[idxPrivacySegment].push(inputDataC[idxPrivacySegment][rIdx]);
//    }
//    if (!inputDataC[idxPrivacySegment].length){
//        inputDataC[idxPrivacySegment]= [];
//    }
//    else{
//        inputDataC[idxPrivacySegment].splice(rIdx, 1);
//
//    }
//    return [inputDataC, inputDataO];
//}
//
//function deleteRule(inputData, idxPrivacySegment, rIdx) {
//    inputData[idxPrivacySegment].splice(rIdx, 1);
//    return inputData;
//}
//
//function approveExtractedRule(data, approvedRules, idxPrivacySegment, rIdx) {
//    if (typeof approvedRules[idxPrivacySegment] == "undefined") {
//        approvedRules[idxPrivacySegment] = [data[idxPrivacySegment][rIdx]];
//    } else {
//        approvedRules[idxPrivacySegment].push(data[idxPrivacySegment][rIdx]);
//    }
//    data[idxPrivacySegment].splice(rIdx, 1);
//    return data
//}
//
//function updateApprovedRulesContainer(approvedRulesList, idxPrivacySegment) {
//    let attrObj = {
//        "rule": "rule",
//        "action": "action",
//        "personalInformationType": "data",
//        "purpose": "purpose"
//    };
//    if (typeof approvedRulesList[idxPrivacySegment] == "undefined" || !approvedRulesList[idxPrivacySegment].length) {
//        approvedRulesList[idxPrivacySegment] = [];
//        document.getElementById('approved-rules-ct').innerHTML = `<div id="approved-rules-ct"><h3>
//                            0 Approved Rule(s)
//                        </h3></div>`;
//
//    }
//    else {
//        let html = `<h3>${approvedRulesList[idxPrivacySegment].length} Approved Rule(s)</h3>`;
//        for (let ruleIdx = 0; ruleIdx < approvedRulesList[idxPrivacySegment].length; ruleIdx++) {  // initial value
//            html +=
//                `<h4 id="approved-rule-ct-header">
//           Rule ${ruleIdx + 1}
//           <a href="#modal-container-unconfirm-rule-${ruleIdx}" data-toggle="modal" type="button"
//               class="btn btn-outline-primary" id="unconfirm_rule_${ruleIdx}">
//            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-excel-fill" viewBox="0 0 16 16">
//              <path d="M9.293 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.707A1 1 0 0 0 13.707 4L10 .293A1 1 0 0 0 9.293 0zM9.5 3.5v-2l3 3h-2a1 1 0 0 1-1-1zM5.884 6.68 8 9.219l2.116-2.54a.5.5 0 1 1 .768.641L8.651 10l2.233 2.68a.5.5 0 0 1-.768.64L8 10.781l-2.116 2.54a.5.5 0 0 1-.768-.641L7.349 10 5.116 7.32a.5.5 0 1 1 .768-.64z"/>
//            </svg>
//           </a>
//           <a href="#modal-container-delete-approved-rule-${ruleIdx}" data-toggle="modal" type="button"
//               class="btn btn-outline-primary" id="delete-approved-rule-${ruleIdx}">
//               <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event"
//                   fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                   <path fill-rule="evenodd"
//                       d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z">
//                   </path>
//               </svg>
//           </a>
//       </h4>
//       <div aria-hidden="true" aria-labelledby="labelConfirmRule" class="modal fade"
//        id="modal-container-unconfirm-rule-${ruleIdx}" role="dialog">
//        <div class="modal-dialog" role="document">
//           <div class="modal-content">
//               <div class="modal-header">
//                   <h5 class="modal-title" id="labelUnConfirmRule">
//                       Unconfirm Rule
//                   </h5>
//                   <button class="close" data-dismiss="modal" type="button">
//                   <span aria-hidden="true">×</span>
//               </button>
//               </div>
//               <div class="modal-body">
//                   Do you want to unconfirm Rule ${ruleIdx + 1}
//               </div>
//               <div class="modal-footer">
//
//                   <button class="btn btn-primary" data-dismiss="modal" type="button" id="unconfirm-button-rule-${ruleIdx}">
//                       Unconfirm
//                   </button>
//                   <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                       Cancel
//                   </button>
//               </div>
//           </div>
//           </div>
//           </div>
//
//       <div aria-hidden="true" aria-labelledby="labelDeleteRule" class="modal fade"
//            id="modal-container-delete-approved-rule-${ruleIdx}" role="dialog">
//           <div class="modal-dialog" role="document">
//               <div class="modal-content">
//                   <div class="modal-header">
//                       <h5 class="modal-title" id="labelDeleteRule">
//                           Delete Rule
//                       </h5>
//                       <button class="close" data-dismiss="modal" type="button">
//                           <span aria-hidden="true">×</span>
//                       </button>
//                   </div>
//                   <div class="modal-body">
//                       Do you really want to delete approved rule ${ruleIdx + 1} ?
//                   </div>
//                   <div class="modal-footer">
//
//                       <button class="btn btn-primary" data-dismiss="modal" type="button" id="delete-button-approved-rule-${ruleIdx}">
//                           Delete
//                       </button>
//                       <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                           Cancel
//                       </button>
//                   </div>
//               </div>
//           </div>
//       </div>
//       <table class="table table-striped table-hover" id="rule-ct-body">
//           <thead align="right">
//               <tr>
//                   <th scope="col" align="right"><span>attribute</span>
//                   </th>
//                   <th scope="col" align="right">values</th>
//               </tr>
//           </thead>
//           <tbody>`
//            for (let attribute of Object.keys(approvedRulesList[idxPrivacySegment][ruleIdx])) {
//                if (attribute.includes("rule")) {
//                    html += `<tr>
//               <td align="right"><span>${attrObj[attribute]}</span>
//               </td>
//               <td align="right"><span>${approvedRulesList[idxPrivacySegment][ruleIdx][attribute]}</span>
//               </td>
//               </tr>`
//                } else {
//                    for (let valIdx = 0; valIdx < approvedRulesList[idxPrivacySegment][ruleIdx][attribute].length; valIdx++) {
//                        if (valIdx == 0) {
//                            html += `
//                            <tr>
//                            <td align="right"><span>${attrObj[attribute]}</span>
//                        </td>
//                        <td align="right"><span>${approvedRulesList[idxPrivacySegment][ruleIdx][attribute][valIdx]}</span>
//                        `
//                        } else {
//                            html += `
//                    <tr>
//                   <td></td>
//                   <td align="right"><span>${approvedRulesList[idxPrivacySegment][ruleIdx][attribute][valIdx]}</span>
//                   </td>
//                     </tr>`
//                        }
//                    }
//                }
//            }
//            html += `           </tbody>
//                     </table>`;
//
//            document.getElementById('approved-rules-ct').innerHTML = html;
//        }
//    }
//}
//
//function deleteExtractedRule(data, idxPrivacySegment, rIdx) {
//    data[idxPrivacySegment].splice(rIdx, 1);
//    return data;
//}
//
//function nextItem(idxPrivacySegment, data) {
//    idxPrivacySegment = idxPrivacySegment + 1; // increase i by one
//    idxPrivacySegment = idxPrivacySegment % data.segments.length; // if we've gone too high, start from `0` again
//    return idxPrivacySegment; // give us back the item of where we are now
//}
//
//function prevItem(idxPrivacySegment, data) {
//    if (idxPrivacySegment === 0) { // i would become 0
//        idxPrivacySegment = data.segments.length; // so put it at the other end of the array
//    }
//    idxPrivacySegment = idxPrivacySegment - 1; // decrease by one
//    return idxPrivacySegment; // give us back the item of where we are now
//}
//
//function getResponse() {
//    return fetch('http://localhost:8080/save/get',
//        {
//            method: "GET",
//            headers: {
//                'Content-Type': 'application/json',
//            },
//        })
//        .then((response) => response.json())
//        .then((responseData) => {
//            //console.log(responseData);
//            return responseData;
//        })
//        .catch(error => console.log(error));
//}
//
//function getTreeResponse(attribute) {
//    return fetch('http://localhost:8080/save/tree',
//        {
//            method: "POST",
//            body: JSON.stringify({type: attribute}),
//            headers: {
//                'Content-Type': 'application/json',
//            },
//        })
//        .then((response) => response.text())
//        .then((responseData) => {
//            return responseData;
//        })
//        .catch(error => console.log(error));
//}
//
//function initSegmentContainer(data, idxPrivacySegment) {
//    document.getElementById('segment-text').textContent = data.segments[idxPrivacySegment]; // initial value
//    document.getElementById('segment-id').innerHTML = `<h3>Segment ${idxPrivacySegment + 1} of ${data.segments.length}
//<!--                           <button type="button" class="btn btn-outline-primary" id="approve-segment">
//                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-check-circle-fill svg-event" viewBox="0 0 16 16">
//                                    <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>
//                                </svg>
//                            </button>
//                            <button type="button" class="btn btn-outline-primary" id="ignore-segment">
//                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-eye-slash-fill svg-event" viewBox="0 0 16 16">
//                                  <path d="m10.79 12.912-1.614-1.615a3.5 3.5 0 0 1-4.474-4.474l-2.06-2.06C.938 6.278 0 8 0 8s3 5.5 8 5.5a7.029 7.029 0 0 0 2.79-.588zM5.21 3.088A7.028 7.028 0 0 1 8 2.5c5 0 8 5.5 8 5.5s-.939 1.721-2.641 3.238l-2.062-2.062a3.5 3.5 0 0 0-4.474-4.474L5.21 3.089z"/>
//                                  <path d="M5.525 7.646a2.5 2.5 0 0 0 2.829 2.829l-2.83-2.829zm4.95.708-2.829-2.83a2.5 2.5 0 0 1 2.829 2.829zm3.171 6-12-12 .708-.708 12 12-.708.708z"/>
//                                </svg>
//                            </button>
//                            <button type="button" class="btn btn-outline-primary" id="flag-segment">
//                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-flag-fill svg-event" viewBox="0 0 16 16">
//                              <path d="M14.778.085A.5.5 0 0 1 15 .5V8a.5.5 0 0 1-.314.464L14.5 8l.186.464-.003.001-.006.003-.023.009a12.435 12.435 0 0 1-.397.15c-.264.095-.631.223-1.047.35-.816.252-1.879.523-2.71.523-.847 0-1.548-.28-2.158-.525l-.028-.01C7.68 8.71 7.14 8.5 6.5 8.5c-.7 0-1.638.23-2.437.477A19.626 19.626 0 0 0 3 9.342V15.5a.5.5 0 0 1-1 0V.5a.5.5 0 0 1 1 0v.282c.226-.079.496-.17.79-.26C4.606.272 5.67 0 6.5 0c.84 0 1.524.277 2.121.519l.043.018C9.286.788 9.828 1 10.5 1c.7 0 1.638-.23 2.437-.477a19.587 19.587 0 0 0 1.349-.476l.019-.007.004-.002h.001"/>
//                            </svg>
//                            </button>-->
//                            </h3>`;
//}
//
////function initDocumentTitle(data) {
////    document.getElementById('document-title').innerHTML = `<h1 align="center" id="document-title">${data.companyName} policy
////                                                      <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-link-45deg" viewBox="0 0 16 16">
////                                                        <a href=${data.companyUrl}>
////                                                        <path d="M4.715 6.542 3.343 7.914a3 3 0 1 0 4.243 4.243l1.828-1.829A3 3 0 0 0 8.586 5.5L8 6.086a1.002 1.002 0 0 0-.154.199 2 2 0 0 1 .861 3.337L6.88 11.45a2 2 0 1 1-2.83-2.83l.793-.792a4.018 4.018 0 0 1-.128-1.287z"/>
////                                                        <path d="M6.586 4.672A3 3 0 0 0 7.414 9.5l.775-.776a2 2 0 0 1-.896-3.346L9.12 3.55a2 2 0 1 1 2.83 2.83l-.793.792c.112.42.155.855.128 1.287l1.372-1.372a3 3 0 1 0-4.243-4.243L6.586 4.672z"/>
////                                                      </svg></h1>`
////}
//
//function initRulesContainer(data, idxPrivacySegment) {
//    let modalsHtml = ``,
//        attrObj = {
//            "rule": "rule",
//            "action": "action",
//            "personalInformationType": "data",
//            "purpose": "purpose"
//        },html = `<h3>${data[idxPrivacySegment].length} Suggested Rule(s)</h3>`;
//
//    for (let ruleIdx = 0; ruleIdx < data[idxPrivacySegment].length; ruleIdx++) {  // initial value
//        html +=
//            `<h4 id="rule-ct-header">
//       Rule ${ruleIdx + 1}
//       <a data-toggle="modal" href="#modal-container-confirm-rule-${ruleIdx}" type="button"
//           class="btn btn-outline-primary" id="confirm_rule_${ruleIdx}">
//           <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-file-earmark-check-fill"
//               fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//               <path fill-rule="evenodd"
//                   d="M2 2a2 2 0 0 1 2-2h5.293A1 1 0 0 1 10 .293L13.707 4a1 1 0 0 1 .293.707V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm7.5 1.5v-2l3 3h-2a1 1 0 0 1-1-1zm1.354 4.354a.5.5 0 0 0-.708-.708L7.5 9.793 6.354 8.646a.5.5 0 1 0-.708.708l1.5 1.5a.5.5 0 0 0 .708 0l3-3z">
//               </path>
//           </svg>
//       </a>
//       <a data-toggle="modal" href="#modal-container-delete-rule-${ruleIdx}" type="button"
//           class="btn btn-outline-primary" id="delete_rule_${ruleIdx}">
//           <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event"
//               fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//               <path fill-rule="evenodd"
//                   d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z">
//               </path>
//           </svg>
//       </a>
//       <a data-toggle="modal" type="button"
//           class="btn btn-outline-primary" id="add-rule">
//           <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-file-earmark-plus-fill"
//               fill="currentColor" xmlns="http://www.w3.org/2000/svg" id="add-rule">
//               <path fill-rule="evenodd"
//                   d="M2 2a2 2 0 0 1 2-2h5.293A1 1 0 0 1 10 .293L13.707 4a1 1 0 0 1 .293.707V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm7.5 1.5v-2l3 3h-2a1 1 0 0 1-1-1zM8.5 7a.5.5 0 0 0-1 0v1.5H6a.5.5 0 0 0 0 1h1.5V11a.5.5 0 0 0 1 0V9.5H10a.5.5 0 0 0 0-1H8.5V7z">
//               </path>
//           </svg>
//       </a>
//   </h4>
//   <div aria-hidden="true" aria-labelledby="labelConfirmRule" class="modal fade"
//    id="modal-container-confirm-rule-${ruleIdx}" role="dialog">
//    <div class="modal-dialog" role="document">
//       <div class="modal-content">
//           <div class="modal-header">
//               <h5 class="modal-title" id="labelConfirmRule">
//                   Approve Rule
//               </h5>
//               <button class="close" data-dismiss="modal" type="button">
//               <span aria-hidden="true">×</span>
//           </button>
//           </div>
//           <div class="modal-body">
//               Do you want to approve Rule ${ruleIdx + 1}
//           </div>
//           <div class="modal-footer">
//
//               <button class="btn btn-primary" type="button" data-dismiss="modal" id="approve-button-rule-${ruleIdx}">
//                   Approve
//               </button>
//               <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                   Cancel
//               </button>
//           </div>
//       </div>
//       </div>
//       </div>
//
//   <div aria-hidden="true" aria-labelledby="labelDeleteRule" class="modal fade"
//        id="modal-container-delete-rule-${ruleIdx}" role="dialog">
//       <div class="modal-dialog" role="document">
//           <div class="modal-content">
//               <div class="modal-header">
//                   <h5 class="modal-title" id="labelDeleteRule">
//                       Delete Rule
//                   </h5>
//                   <button class="close" data-dismiss="modal" type="button">
//                       <span aria-hidden="true">×</span>
//                   </button>
//               </div>
//               <div class="modal-body">
//                   Do you really want to delete rule ${ruleIdx + 1} ?
//               </div>
//               <div class="modal-footer">
//
//                   <button class="btn btn-primary" data-dismiss="modal" type="button" id="delete-button-rule-${ruleIdx}">
//                       Delete
//                   </button>
//                   <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                       Cancel
//                   </button>
//               </div>
//           </div>
//       </div>
//   </div>
//   <table class="table table-striped table-hover" id="rule-ct-body-${ruleIdx}">
//       <thead align="right">
//               <tr>
//          <th scope="col" align="right"><span>attribute</span>
//              <a data-toggle="modal" href="#modal-container-add-extracted-attribute"  id="add-attribute-extracted-${ruleIdx}">
//                  <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-node-plus svg-event"
//                      fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                      <path fill-rule="evenodd"
//                          d="M11 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8zM6.025 7.5a5 5 0 1 1 0 1H4A1.5 1.5 0 0 1 2.5 10h-1A1.5 1.5 0 0 1 0 8.5v-1A1.5 1.5 0 0 1 1.5 6h1A1.5 1.5 0 0 1 4 7.5h2.025zM11 5a.5.5 0 0 1 .5.5v2h2a.5.5 0 0 1 0 1h-2v2a.5.5 0 0 1-1 0v-2h-2a.5.5 0 0 1 0-1h2v-2A.5.5 0 0 1 11 5zM1.5 7a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1z" />
//                  </svg>
//              </a>
//          </th>
//          <th scope="col" align="right">values</th>
//      </tr>
//       </thead>
//       <tbody>`
//        for (let attribute of Object.keys(data[idxPrivacySegment][ruleIdx])) {
//            if (attribute.includes("rule")) {
//                html += `<tr class="tr-${attrObj[attribute]}" id="tr-${attrObj[attribute]}">
//           <td align="right"><span>${attrObj[attribute]}</span>
//           </td>
//           <td align="right"><span>${data[idxPrivacySegment][ruleIdx][attribute]}</span>
//               <a data-toggle="modal" href="#modal-container-edit-${attrObj[attribute]}-type" id="btn-modal-container-extracted-edit-${attrObj[attribute]}-${ruleIdx}-type">
//                   <svg width="1em" height="1em" viewBox="0 0 16 16"
//                       class="bi bi-pencil-square svg-event" fill="currentColor"
//                       xmlns="http://www.w3.org/2000/svg">
//                       <path
//                           d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z" />
//                       <path fill-rule="evenodd"
//                           d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z" />
//                   </svg>
//               </a>
//           </td>
//           </tr>`
//            } else {
//                for (let valIdx = 0; valIdx < data[idxPrivacySegment][ruleIdx][attribute].length; valIdx++) {
//                    if (valIdx == 0) {
//                        html += `
//                <tr class="tr-${attrObj[attribute]}" id="tr-${attrObj[attribute]}-${ruleIdx}-${valIdx}">
//                        <td align="right"><span>${attrObj[attribute]}</span>
//               <a href="#modal-container-delete-${attrObj[attribute]}-attribute" id="delete-extracted-all-${attrObj[attribute]}-${ruleIdx}-attribute">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event"
//                                fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd"
//                                    d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z" />
//                            </svg>
//                        </a>
//                        <a data-toggle="modal" href="#modal-container-add-extracted-${attrObj[attribute]}-attribute" id="add-value-extracted-attribute-${attrObj[attribute]}-${ruleIdx}">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-node-plus svg-event"
//                                fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd"
//                                    d="M11 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8zM6.025 7.5a5 5 0 1 1 0 1H4A1.5 1.5 0 0 1 2.5 10h-1A1.5 1.5 0 0 1 0 8.5v-1A1.5 1.5 0 0 1 1.5 6h1A1.5 1.5 0 0 1 4 7.5h2.025zM11 5a.5.5 0 0 1 .5.5v2h2a.5.5 0 0 1 0 1h-2v2a.5.5 0 0 1-1 0v-2h-2a.5.5 0 0 1 0-1h2v-2A.5.5 0 0 1 11 5zM1.5 7a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1z" />
//                            </svg>
//                        </a>
//                    </td>
//                    <td align="right"><span>${data[idxPrivacySegment][ruleIdx][attribute][valIdx]}</span>
//               <a href="#modal-container-delete-${attrObj[attribute]}-type" id="delete-from-extracted-attribute-${attrObj[attribute]}-${ruleIdx}-${valIdx}">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event"
//                                fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                                <path fill-rule="evenodd"
//                                    d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z" />
//                            </svg>
//                        </a>
//                     <a data-toggle="modal" href="#modal-container-edit-${attrObj[attribute]}-type" id="btn-modal-container-extracted-edit-${attrObj[attribute]}-${ruleIdx}-${valIdx}-type">
//                            <svg width="1em" height="1em" viewBox="0 0 16 16"
//                                class="bi bi-pencil-square svg-event" fill="currentColor"
//                                xmlns="http://www.w3.org/2000/svg">
//                                <path
//                                    d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z" />
//                                <path fill-rule="evenodd"
//                                    d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z" />
//                            </svg>
//                        </a>`
//                    } else {
//                        html += `
//                <tr class="tr-${attrObj[attribute]}" id="tr-${attrObj[attribute]}-${ruleIdx}-${valIdx}">
//               <td></td>
//               <td align="right"><span>${data[idxPrivacySegment][ruleIdx][attribute][valIdx]}</span>
//               <a href="#modal-container-delete-${attrObj[attribute]}-type" id="delete-from-extracted-attribute-${attrObj[attribute]}-${ruleIdx}-${valIdx}">
//                       <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-trash-fill svg-event"
//                           fill="currentColor" xmlns="http://www.w3.org/2000/svg">
//                           <path fill-rule="evenodd"
//                               d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z" />
//                       </svg>
//                   </a>
//               <a data-toggle="modal" href="#modal-container-edit-${attrObj[attribute]}-type" id="btn-modal-container-extracted-edit-${attrObj[attribute]}-${ruleIdx}-${valIdx}-type">
//                       <svg width="1em" height="1em" viewBox="0 0 16 16"
//                           class="bi bi-pencil-square svg-event" fill="currentColor"
//                           xmlns="http://www.w3.org/2000/svg">
//                           <path
//                               d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456l-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z" />
//                           <path fill-rule="evenodd"
//                               d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z" />
//                       </svg>
//                   </a>
//               </td>
//                 </tr>`
//                    }
//                }
//            }
//            modalsHtml +=`<div aria-hidden="true" aria-labelledby="labelEdit${attrObj[attribute]}Type" class="modal fade"
//                         id="modal-container-edit-${attrObj[attribute]}-type" role="dialog">
//                        <div class="modal-dialog" role="document">
//                            <div class="modal-content">
//                                <div class="modal-header">
//                                    <h5 class="modal-title" id="labelEdit${attrObj[attribute]}Type">
//                                        Edit ${attrObj[attribute]} value
//                                    </h5>
//                                    <button class="close" data-dismiss="modal" type="button">
//                                        <span aria-hidden="true">×</span>
//                                    </button>
//                                </div>
//                                <div style="max-height: calc(100vh - 210px); overflow-y: auto;" class="modal-body">
//                                    <div class="rulevaluetree"></div>
//                                </div>
//                                <div class="modal-footer">
//
//                                    <button class="btn btn-primary" type="button"  data-dismiss="modal" id="edit-extracted-value-${attrObj[attribute]}">
//                                        Edit
//                                    </button>
//                                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                                        Close
//                                    </button>
//                                </div>
//                            </div>
//
//                        </div>
//
//                    </div>
//</div>
//<div aria-hidden="true" aria-labelledby="labelDelete${attrObj[attribute]}Type" class="modal fade"
//                         id="modal-container-delete-${attrObj[attribute]}-type" role="dialog">
//                        <div class="modal-dialog" role="document">
//                            <div class="modal-content">
//                                <div class="modal-header">
//                                    <h5 class="modal-title" id="labelDelete${attrObj[attribute]}Type">
//                                        Delete ${attrObj[attribute]} value
//                                    </h5>
//                                    <button class="close" data-dismiss="modal" type="button">
//                                        <span aria-hidden="true">×</span>
//                                    </button>
//                                </div>
//                                <div class="modal-body">
//                                    Do you really want to delete selected value from ${attrObj[attribute]} ?
//                                </div>
//                                <div class="modal-footer">
//
//                                    <button class="btn btn-primary" type="button" data-dismiss="modal"  id="delete-from-attribute-${attrObj[attribute]}-value">
//                                        Delete
//                                    </button>
//                                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                                        Close
//                                    </button>
//                                </div>
//                            </div>
//
//                        </div>
//
//                    </div>
//</div>
//<div aria-hidden="true" aria-labelledby="labelDelete${attrObj[attribute]}Attribute" class="modal fade"
//                         id="modal-container-delete-${attrObj[attribute]}-attribute" role="dialog">
//                        <div class="modal-dialog" role="document">
//                            <div class="modal-content">
//                                <div class="modal-header">
//                                    <h5 class="modal-title" class="attributeDelete${attrObj[attribute]}Type">
//                                        Delete ${attrObj[attribute]} value
//                                    </h5>
//                                    <button class="close" data-dismiss="modal" type="button">
//                                        <span aria-hidden="true">×</span>
//                                    </button>
//                                </div>
//                                <div class="modal-body">
//                                    Do you really want to delete ${attrObj[attribute]} ?
//                                </div>
//                                <div class="modal-footer">
//
//                                    <button class="btn btn-primary" type="button" id="delete-all-attribute-${attrObj[attribute]}-value" data-dismiss="modal">
//                                        Delete
//                                    </button>
//                                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                                        Close
//                                    </button>
//                                </div>
//                            </div>
//
//                        </div>
//
//                    </div>
//</div>
//<div aria-hidden="true" aria-labelledby="labelAdd${attrObj[attribute]}Attribute" class="modal fade"
//                         id="modal-container-add-extracted-${attrObj[attribute]}-attribute" role="dialog">
//                        <div class="modal-dialog" role="document">
//                            <div class="modal-content">
//                                <div class="modal-header">
//                                    <h5 class="modal-title" class="attributeAdd${attrObj[attribute]}Type">
//                                        Add ${attrObj[attribute]} value
//                                    </h5>
//                                    <button class="close" data-dismiss="modal" type="button">
//                                        <span aria-hidden="true">×</span>
//                                    </button>
//                                </div>
//                                <div style="max-height: calc(100vh - 210px); overflow-y: auto;" class="modal-body">
//                                <div class="rulevaluetree"></div>
//                                </div>
//                                <div class="modal-footer">
//
//                                    <button class="btn btn-primary" data-dismiss="modal" type="button" id="add-value-to-attribute-extracted">
//                                        Add
//                                    </button>
//                                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                                        Close
//                                    </button>
//                                </div>
//                            </div>
//
//                        </div>
//
//                    </div>
//</div>
//         <div aria-hidden="true" aria-labelledby="addLabel" class="modal fade" id="modal-container-add-extracted-attribute"
//         role="dialog">
//        <div class="modal-dialog" role="document">
//            <div class="modal-content">
//                <div class="modal-header">
//                    <h5 class="modal-title" id="addLabel">
//                        Add Attribute Entry
//                    </h5>
//                    <button class="close" data-dismiss="modal" type="button">
//                        <span aria-hidden="true">×</span>
//                    </button>
//                </div>
//                <div class="modal-body">
//                    <div class="attributetree"></div>
//                </div>
//                <div class="modal-footer">
//
//                    <button href= "#modal-container-add-extracted-value" class="btn btn-primary" type="button" id="add-value-to-attribute-extracted" data-toggle="modal" data-dismiss="modal">
//                        Add
//                    </button>
//                    <button class="btn btn-secondary" data-dismiss="modal" type="button">
//                        Close
//                    </button>
//                </div>
//            </div>
//
//        </div>
//        </div>
//        <div aria-hidden="true" aria-labelledby="addValue" class="modal fade" id="modal-container-add-extracted-value"
//         role="dialog">
//        <div class="modal-dialog" role="document">
//            <div class="modal-content">
//                <div class="modal-header">
//                    <h5 class="modal-title" id="addValue">
//                        Add Attribute Value
//                    </h5>
//                    <button class="close" data-dismiss="modal" type="button">
//                        <span aria-hidden="true">×</span>
//                    </button>
//                </div>
//                <div style="max-height: calc(100vh - 210px); overflow-y: auto;" class="modal-body">
//                    <div class="rulevaluetree"></div>
//                </div>
//                <div class="modal-footer">
//
//                    <button class="btn btn-primary" type="button" id="add-extracted-value" data-dismiss="modal">
//                        Confirm
//                    </button>
//                    <button href="#modal-container-add-extracted-attribute" class="btn btn-secondary" data-toggle="modal" data-dismiss="modal" type="button">
//                        Close
//                    </button>
//                </div>
//            </div>
//
//        </div>
//        </div>`;
//        }
//        html += `           </tbody>
//                 </table>`;
//
//    }
//
//    document.getElementById('rule-ct').innerHTML = html;
//    document.getElementById('rule-ct').insertAdjacentHTML('beforeend',modalsHtml);
//    return data;
//}