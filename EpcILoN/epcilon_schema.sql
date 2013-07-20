
    create table EventToPublish (
        id integer auto_increment,
        epc varchar(255) not null,
        eventType varchar(40) not null,
        bizStep varchar(255) not null,
        eventTime timestamp not null,
        lastUpdate timestamp not null,
        owner varchar(255),
        primary key (id)
    );
