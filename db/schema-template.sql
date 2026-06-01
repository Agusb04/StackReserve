-- Ejemplo abstracto: si tu entrega incluye SQL, reemplazalo por un diseño coherente con la API y el modelo ER.
-- Cada FK debe apuntar a la PK de otra tabla y debe quedar trazada en `db/modelo-er.md`.
-- Si una tabla asociativa no tiene atributos propios, preferí una PK compuesta por sus FKs o justificá la excepción.
create table tabla_principal (
    id_tabla_principal bigint primary key,
    descripcion varchar(255) not null
);

create table tabla_secundaria (
    id_tabla_secundaria bigint primary key,
    descripcion varchar(255) not null
);

create table tabla_relacion (
    id_tabla_relacion bigint primary key,
    id_tabla_principal bigint not null,
    id_tabla_secundaria bigint not null,
    estado varchar(32) not null,
    constraint fk_tabla_relacion_principal foreign key (id_tabla_principal) references tabla_principal (id_tabla_principal),
    constraint fk_tabla_relacion_secundaria foreign key (id_tabla_secundaria) references tabla_secundaria (id_tabla_secundaria)
);
