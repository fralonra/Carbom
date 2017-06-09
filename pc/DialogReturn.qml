import QtQuick 2.6
import QtQuick.Controls 1.4
import QtQuick.Dialogs 1.2
import QtQuick.Layouts 1.3

Dialog {
    property var source: []
    property var selection: new Array

    width: 300
    height: 440
    modality: Qt.WindowModal
    standardButtons: StandardButton.Ok | StandardButton.Cancel
    ColumnLayout {
        anchors.fill: parent
        spacing: 10
        CheckBox {
            id: allselect
            text: qsTr("Select All")
            onClicked: {
                list.allselectMode = checked;
            }
        }
        RowLayout {
            anchors.top: allselect.bottom + 10
            anchors.left: parent.left
            anchors.right: parent.right
            spacing: 10
            ListView {
                property bool allselectMode: false

                id: list
                Layout.fillHeight: true
                width: 250
                model: source
                focus: true
                delegate: Rectangle {
                    width: 250
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
                }
            }
        }
    }
}
