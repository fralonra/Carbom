import QtQuick 2.6
import QtQuick.Controls 1.4
import QtQuick.Controls.Styles 1.4

Item {
    property string key
    property string value
    property bool editable
    property bool hasModified: false

    id: root
    Text {
        id: keyText
        text: root.key + ":     "
        color: editable? "red" : "black"
        font.bold: true
        font.pointSize: 15
        anchors.left: root.left
        anchors.margins: 5
        anchors.rightMargin: 20
    }
    TextField {
        id: valueText
        enabled: editable
        x: 140
        y: 3
        width: 250
        text: parent.value
        placeholderText: qsTr("null")
        font.pointSize: 12
        textColor: root.editable? "black" : "grey"
        style: TextFieldStyle {
            background: Rectangle {
                radius: 1
                border.color: "grey"
                border.width: 1
                color: root.editable? "white" : "lightgrey"
            }
        }
        onTextChanged: {
            if (root.value != text) {
                root.value = Qt.binding(function() {
                    return text
                })
                hasModified = true
            } else {
                hasModified = false
            }
        }
    }
}
