create table catalyst
(
    name text primary key
) without rowid;

create table catalyst_item
(
    catalyst_name references catalyst,
    item_registry_name references item,
    primary key (catalyst_name, item_registry_name)
) without rowid;

create table fluid
(
    registry_name text primary key,
    display_name  text,
    nbt           text,
    icon          blob
) without rowid;

create table gregtech_recipe
(
    id              integer primary key,
    amperage        integer,
    config          integer,
    duration        integer,
    voltage         integer,
    fuel_value      integer,
    fuel_multiplier integer,
    fuel_recipe     integer,
    catalyst_name references catalyst
);

create table gregtech_recipe_input_fluid
(
    gregtech_recipe_id references gregtech_recipe,
    fluid_registry_name references item,
    slot   integer not null,
    amount integer,
    primary key (fluid_registry_name, gregtech_recipe_id, slot)
) without rowid;

create table gregtech_recipe_input_item
(
    gregtech_recipe_id references gregtech_recipe,
    item_registry_name references item,
    slot   integer not null,
    amount integer,
    primary key (gregtech_recipe_id, item_registry_name, slot)
) without rowid;

create table gregtech_recipe_input_ore
(
    gregtech_recipe_id references gregtech_recipe,
    ore_name references ore,
    slot   integer not null,
    amount integer,
    primary key (gregtech_recipe_id, ore_name, slot)
) without rowid;

create table gregtech_recipe_output_fluid
(
    gregtech_recipe_id references gregtech_recipe,
    fluid_registry_name references item,
    slot   integer not null,
    amount integer,
    primary key (fluid_registry_name, gregtech_recipe_id, slot)
) without rowid;

create table gregtech_recipe_output_item
(
    gregtech_recipe_id references gregtech_recipe,
    item_registry_name references item,
    slot   integer not null,
    amount integer,
    chance integer,
    primary key (gregtech_recipe_id, item_registry_name, slot)
) without rowid;

create table item
(
    registry_name text primary key,
    display_name  text,
    nbt           text,
    icon          blob
) without rowid;

create table ore
(
    name text not null primary key
) without rowid;

create table ore_item
(
    item_registry_name references item,
    ore_name references ore,
    primary key (ore_name, item_registry_name)
) without rowid;

create table shaped_recipe
(
    id integer primary key,
    output_item_registry_name references item
);

create table shaped_recipe_input_item
(
    item_registry_name references item,
    shaped_recipe_id references shaped_recipe,
    slot integer not null,
    primary key (item_registry_name, shaped_recipe_id, slot)
) without rowid;

create table shaped_recipe_input_ore
(
    ore_name references ore,
    shaped_recipe_id references shaped_recipe,
    slot integer not null,
    primary key (ore_name, shaped_recipe_id, slot)
) without rowid;

create table shapeless_recipe
(
    id integer primary key,
    output_item_registry_name references item
);

create table shapeless_recipe_input_item
(
    item_registry_name references item,
    shapeless_recipe_id references shapeless_recipe,
    slot integer not null,
    primary key (item_registry_name, shapeless_recipe_id, slot)
) without rowid;

create table shapeless_recipe_input_ore
(
    ore_name references ore,
    shapeless_recipe_id references shapeless_recipe,
    slot integer not null,
    primary key (ore_name, shapeless_recipe_id, slot)
) without rowid;
create table aspect
(
    tag  text primary key,
    name text,
    icon blob
) without rowid;
