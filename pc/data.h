#ifndef DATA_H
#define DATA_H
#include <QDebug>
#include <QFile>
#include <QFileInfo>
#include <QMessageBox>
#include <QObject>
#include <QQmlFile>
#include <QtQuick>
#include <QTextStream>
#include <QFileSystemWatcher>

#include "entry.h"
#include "xlsxdocument.h"

class Data : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QString file READ file WRITE setFile NOTIFY fileChanged)
    Q_PROPERTY(QString title READ title WRITE setTitle NOTIFY titleChanged)
    Q_PROPERTY(QStringList list READ list WRITE setList NOTIFY listChanged)
    Q_PROPERTY(QList<Entry> returnList READ returnList WRITE setReturnList NOTIFY returnListChanged)
    Q_PROPERTY(QList<Entry> table READ table WRITE setTable NOTIFY tableChanged)
    Q_PROPERTY(int count READ count WRITE setCount NOTIFY countChanged)
    Q_PROPERTY(int position READ position WRITE setPosition NOTIFY positionChanged)

    Q_PROPERTY(QString epc READ epc WRITE setEpc NOTIFY epcChanged)
    Q_PROPERTY(QString type READ type WRITE setType NOTIFY typeChanged)
    Q_PROPERTY(QString name READ name WRITE setName NOTIFY nameChanged)
    Q_PROPERTY(QString stage READ stage WRITE setStage NOTIFY stageChanged)
    Q_PROPERTY(QString status READ status WRITE setStatus NOTIFY statusChanged)
    Q_PROPERTY(QString time READ time WRITE setTime NOTIFY timeChanged)
    Q_PROPERTY(QString location READ location WRITE setLocation NOTIFY locationChanged)
    Q_PROPERTY(QString keeper READ keeper WRITE setKeeper NOTIFY keeperChanged)
    Q_PROPERTY(QString note READ note WRITE setNote NOTIFY noteChanged)
    Q_PROPERTY(QString loanDate READ loanDate WRITE setLoanDate NOTIFY loanDateChanged)
    Q_PROPERTY(QString returnDate READ returnDate WRITE setReturnDate NOTIFY returnDateChanged)

public:
    explicit Data(QObject *parent = 0);

    QString file() const {return m_file;}
    QString title() const {return m_title;}
    QStringList list() const {return m_list;}
    QList<Entry> returnList() const {return m_returnList;}
    QList<Entry> table() const {return m_table;}
    int count() const {return m_table.size();}
    int position() const {return m_position;}

    QString epc() const {return m_epc;}
    QString type() const {return m_type;}
    QString name() const {return m_name;}
    QString stage() const {return m_stage;}
    QString status() const {return m_status;}
    QString time() const {return m_time;}
    QString location() const {return m_location;}
    QString keeper() const {return m_keeper;}
    QString note() const {return m_note;}
    QString loanDate() const {return m_loanDate;}
    QString returnDate() const {return m_returnDate;}

    Q_INVOKABLE bool isEmpty() const {return m_allData.isEmpty();}
    Q_INVOKABLE bool isFound() const {return !m_result.isEmpty();}

signals:
    void fileChanged();
    void titleChanged();
    void listChanged();
    void returnListChanged();
    void tableChanged();
    void countChanged();
    void positionChanged();

    void epcChanged();
    void typeChanged();
    void nameChanged();
    void stageChanged();
    void statusChanged();
    void timeChanged();
    void locationChanged();
    void keeperChanged();
    void noteChanged();
    void loanDateChanged();
    void returnDateChanged();

public slots:
    void setFile(const QString &arg);
    void setTitle(const QString &arg);
    void setList(const QStringList &arg);
    void setReturnList(const QList<Entry> &arg);
    //void setReturnList();
    void setTable(const QList<Entry> &arg);
    void setCount(const int &arg);
    void setPosition(const int &arg);

    void setEpc(const QString &arg);
    void setType(const QString &arg);
    void setName(const QString &arg);
    void setStage(const QString &arg);
    void setStatus(const QString &arg);
    void setTime(const QString &arg);
    void setLocation(const QString &arg);
    void setKeeper(const QString &arg);
    void setNote(const QString &arg);
    void setLoanDate(const QString &arg);
    void setReturnDate(const QString &arg);

    void open(const QString &file);
    void importXls(const QString &file);
    void save();
    void saveAs(const QString &file);
    void close();
    void prev();
    void next();
    void first();
    void last();
    void find(const QString &arg);
    void reset();
    void add(const QString &arg, bool multi = false);
    void add(const QStringList &arg);
    void remove(const int index, bool multi = false);
    void remove(const QString &epc, bool multi = false);
    void remove(const QList<int> &list);
    void clear();
    void sort();
    void modify(const QString &epc, const QString &data);
    void loan(const QList<int> &list, const QString &data);
    void returnBack(const QList<int> &list);

private:
    bool modified = false;
    bool allShown = true;
    QFileSystemWatcher watcher;

    QStringList m_data = {}; // Current text by lines
    QStringList m_allData = {}; // All text in file by lines
    QStringList m_allEpc = {}; // All epcs in file
    QList<Entry> m_returnList = {};
    QList<Entry> m_allTable = {}; // All data
    QList<Entry> m_result = {}; // Data of search result

    QString m_file = "";
    QString m_title = "";
    QStringList m_list = {}; // Current epcs
    QList<Entry> m_table; // Current data
    int m_count = 0;
    int m_position = -1;

    QString m_epc = "";
    QString m_type = "";
    QString m_name = "";
    QString m_stage = "";
    QString m_status = "";
    QString m_time = "";
    QString m_location = "";
    QString m_keeper = "";
    QString m_note = "";
    QString m_loanDate = "";
    QString m_returnDate = "";

    void setData(const QStringList &arg);
    void saveData(const QString &fileName);
    const QString toString();
};

#endif // DATA_H
