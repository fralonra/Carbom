#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QTranslator>
#include <data.h>
#include <datamodel.h>
#include <entry.h>

int main(int argc, char *argv[])
{
    QTranslator translator;
    translator.load(QString("carbom-reader_") + QLocale::system().name());

    QGuiApplication app(argc, argv);
    app.installTranslator(&translator);
    qmlRegisterType<Data>("CarbomReader", 1, 0, "Data");
    qmlRegisterType<DataModel>("CarbomReader", 1, 0, "DataModel");
    qRegisterMetaType<QList<Entry>>("QList<Entry>");

    QQmlApplicationEngine engine;
    engine.load(QUrl(QStringLiteral("qrc:/main.qml")));

    return app.exec();
}
