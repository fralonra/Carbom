#ifndef DATAMODEL_H
#define DATAMODEL_H

#include <QAbstractTableModel>

#include "entry.h"

class DataModel : public QAbstractTableModel
{
    Q_OBJECT
    Q_PROPERTY(QList<Entry> source READ source WRITE setSource NOTIFY sourceChanged)

public:
    enum Role {
        EpcRole = Qt::UserRole + 1,
        TypeRole,
        NameRole,
        StageRole,
        StatusRole,
        TimeRole,
        LocationRole,
        KeeperRole,
        NoteRole,
        LoanDateRole,
        ReturnDateRole,
        IndexRole
    };

public:
    explicit DataModel(QObject *parent = 0);
    explicit DataModel(const QList<Entry> &data);
    DataModel(const DataModel &other);
    DataModel &operator =(const DataModel &other);

    // Header:
    QVariant headerData(int section, Qt::Orientation orientation, int role = Qt::DisplayRole) const override;

    // Basic functionality:
    int rowCount(const QModelIndex &parent = QModelIndex()) const override;
    int columnCount(const QModelIndex &parent = QModelIndex()) const override;

    QVariant data(const QModelIndex &index, int role = Qt::DisplayRole) const override;
    QHash<int, QByteArray> roleNames() const;

    QList<Entry> source() const {return m_source;}

public slots:
    void setSource(const QList<Entry> &arg);
    //void add();
    //void remove();

signals:
    void sourceChanged();

private:
    QList<Entry> m_source;
};

#endif // DATAMODEL_H
