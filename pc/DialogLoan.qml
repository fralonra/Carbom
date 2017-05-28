import QtQuick 2.6
import QtQuick.Controls 1.4
import QtQuick.Dialogs 1.2
import QtQuick.Layouts 1.3

Dialog {
    property string entry
    property var source: []
    property var selection: new Array

    width: 600
    height: 440
    modality: Qt.WindowModal
    standardButtons: StandardButton.Ok | StandardButton.Cancel
    ColumnLayout {
        anchors.fill: parent
        spacing: 10
        CheckBox {
            id: allselect
            text: qsTr("Select All")
            onCheckedChanged: list.allselectMode = checked
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
                            checked: allselect.checked
                            onCheckedChanged: {
                                if (checked)
                                    selection.push(index)
                                else {
                                    var l = new Array
                                    for (var i = 0; i < selection.length; ++i) {
                                        if (selection[i] !== index)
                                            l.push(selection[i])
                                        selection = l
                                    }
                                }
                            }
                            onAllselectModeChanged: checked = allselect.checked
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
            ColumnLayout {
                anchors.top: parent.top
                anchors.right: parent.right
                spacing: 15
                EntryText {
                    id: keeper
                    name: qsTr("Keeper")
                }
                EntryDate {
                    id: loanDate
                    name: qsTr("Loan Date")
                }
                EntryDate {
                    id: returnDate
                    name: qsTr("Return Date")
                }
                EntryText {
                    id: note
                    name: qsTr("Note")
                }
            }
        }
    }
    onAccepted: {
        if (keeper.text != "")
            entry = entry + "KEEPER:" + keeper.text + "&"
        if (loanDate.text == "")
            entry = entry + "LOAN_DATE:" + Qt.formatDate(new Date(), "yyyy-MM-dd") + "&"
        else if (loanDate.text != "")
            entry = entry + "LOAN_DATE:" + loanDate.text + "&"
        if (returnDate.text != "")
            entry = entry + "RETURN_DATE:" + returnDate.text + "&"
        if (note.text != "")
            entry = entry + "NOTE:" + note.text + "&"
    }
}
