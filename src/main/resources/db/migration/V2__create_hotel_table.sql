CREATE TABLE hotel (
   id SMALLINT NOT NULL PRIMARY KEY,
   name VARCHAR(255) NOT NULL
);

INSERT INTO hotel (id, name)
VALUES
    (1, 'Haliç Park'),
    (2, 'Çamlık 87'),
    (3, 'Diğer');

ALTER TABLE participant
    ADD COLUMN hotel_id SMALLINT;

ALTER TABLE participant
    ADD CONSTRAINT fk_participant_hotel
        FOREIGN KEY (hotel_id)
            REFERENCES hotel(id);