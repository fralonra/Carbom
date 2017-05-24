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
        }
        ListView {
            id: list
            Layout.fillHeight: true
            width: 250
            model: source
            focus: true
            delegate: Rectangle {
                property bool selected: checkbox.checked
                width: 250
                height: 20
                color: ListView.isCurrentItem ? "transparent" : "white"
                RowLayout {
                    CheckBox {
                        id: checkbox
                        checked: allselect.checked
                        onCheckedChanged: {
                            if (checked)
                                selection.push(index)
                            else {
                                var list = new Array
                                for (var i = 0; i < selection.length; ++i) {
                                    if (selection[i] !== index)
                                        list.push(selection[i])
                                    selection = list
                                }
                            }
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
