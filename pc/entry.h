#ifndef ENTRY_H
#define ENTRY_H

#include <QObject>
#include <QMap>
#include <QRegularExpression>

class Entry : public QObject
{
    Q_OBJECT
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
    enum Index {
        Epc,
        Type,
        Name,
        Stage,
        Status,
        Time,
        Location,
        Keeper,
        Note,
        LoanDate,
        ReturnDate,
        IndexCount
    };
    static const QMap<Index, QString> indexText;
    static const QString entryDiv;
    static const QString dataDiv;
    static const QString storedStr;

public:
    explicit Entry(QObject *parent = 0);
    Entry(const QString &data, QObject *parent = 0);
    Entry(const Entry &other);
    Entry &operator =(const Entry &other);
    bool operator ==(const Entry &other) const;

    void init(const QString &data);
    void modify(const QString &data);
    void insert(const Index index, const QString &value);
    const QString value(const Index index) const;
    const QString value(const int index) const;
    const QString value(const QString &index) const;
    const QString toString();
    bool isStored() const;

    QString epc() const {return value(Epc);}
    QString type() const {return value(Type);}
    QString name() const {return value(Name);}
    QString stage() const {return value(Stage);}
    QString status() const {return value(Status);}
    QString time() const {return value(Time);}
    QString location() const {return value(Location);}
    QString keeper() const {return value(Keeper);}
    QString note() const {return value(Note);}
    QString loanDate() const {return value(LoanDate);}
    QString returnDate() const {return value(ReturnDate);}
    int sn() const;

signals:
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

private:
    QMap<Index, QString> m_data;

private:
    const QString phraseData(const QString &key, const QString &data);
    const QString phraseData(const Index &index, const QString &data);
};

#endif // ENTRY_H
