import QtQuick 2.6
import QtQuick.Controls 1.4
import QtQuick.Dialogs 1.2
import QtQuick.Layouts 1.3

import CarbomReader 1.0

Dialog {
    property var list: []
    property var selection: new Array

    width: 500
    height: 440
    modality: Qt.WindowModal
    standardButtons: StandardButton.Ok | StandardButton.Cancel
    ColumnLayout {
        anchors.fill: parent
        spacing: 10
        TableView {
            property bool allselectMode: false

            id: table
            anchors.left: parent.left
            Layout.fillWidth: true
            Layout.fillHeight: true
            model: datamodel
            selectionMode: SelectionMode.NoSelection

            CheckBox {
                id: allselect
                onClicked: {
                    table.allselectMode = checked;
                }
            }
            TableViewColumn {
                width: 30
                horizontalAlignment: Text.AlignHCenter
                delegate: CheckBox {
                    property bool allselectMode: table.allselectMode
                    id: checkbox
                    onClicked: {
                        if (selection.length !== source.length)
                            allselect.checked = false;
                        else
                            allselect.checked = true;
                    }
                    onCheckedChanged: {
                        if (checked)
                            selection.push(styleData.row - 1)
                        else {
                            var l = new Array
                            for (var i = 0; i < selection.length; ++i) {
                                if (selection[i] !== styleData.row - 1)
                                    l.push(selection[i])
                            }
                            selection = l
                        }
                    }
                    onAllselectModeChanged: {
                        checked = allselectMode
                    }
                }
            }
            TableViewColumn {
                role: "epc"
                title: qsTr("EPC")
                width: 200
                delegate: Rectangle {
                    color: styleData.row - 1 == table.currentRow ? "lightsteelblue" : "transparent"
                    Text {
                        text: styleData.value
                        color: "black"
                    }
                }
            }
            TableViewColumn {
                role: "type"
                title: qsTr("Type")
                width: 100
                delegate: Rectangle {
                    color: styleData.row - 1 == table.currentRow ? "lightsteelblue" : "transparent"
                    Text {
                        text: styleData.value
                        color: "black"
                    }
                }
            }
            TableViewColumn {
                role: "keeper"
                title: qsTr("Keeper")
                width: 100
                delegate: Rectangle {
                    color: styleData.row - 1 == table.currentRow ? "lightsteelblue" : "transparent"
                    Text {
                        text: styleData.value
                        color: "black"
                    }
                }
            }
            /*ListView {
                property bool allselectMode: false

                id: list
                Layout.fillHeight: true
                model: source
                focus: true
                delegate: Rectangle {
                    width: 450
                    height: 20
                    color: ListView.isCurrentItem ? "transparent" : "white"
                    RowLayout {
                        CheckBox {
                            property bool allselectMode: list.allselectMode
                            id: checkbox
                            onClicked: {
                                if (selection.length !== source.length)
                                    allselect.checked = false;
                                else
                                    allselect.checked = true;
                            }
                            onCheckedChanged: {
                                if (checked)
                                    selection.push(index)
                                else {
                                    var l = new Array
                                    for (var i = 0; i < selection.length; ++i) {
                                        if (selection[i] !== index)
                                            l.push(selection[i])
                                    }
                                    selection = l
                                }
                            }
                            onAllselectModeChanged: {
                                checked = allselectMode
                            }
                        }
                        Text {
                            text: modelData
                            MouseArea {
                                anchors.fill: parent
                                onClicked: list.currentIndex = index
                            }
                        }
                    }
                }
                highlight: Rectangle {
                    color: "lightsteelblue"
                    radius: 2
                }*/
        }
    }
    DataModel {
        id: datamodel
        source: list
    }
}
