import QtQuick 2.6
import QtQuick.Controls 1.4

ToolButton {
    width: 24
    height: 24
    Text {
        text: parent.text
        font.pointSize: 10
        anchors.top: parent.bottom // Placing text in bottom
        anchors.margins: 2 // Leaving space between text and borders  (optional)
        anchors.horizontalCenter: parent.horizontalCenter // Centering text
        renderType: Text.NativeRendering // Rendering type (optional)
    }
}
