create table catalyst
(
    name text primary key
) without rowid;

create table catalyst_item
(
    catalyst_name references catalyst,
    item_registry_name text,
    item_nbt text,
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt),
    primary key (catalyst_name, item_registry_name)
) without rowid;

create table fluid
(
    registry_name text,
    display_name  text,
    nbt           text,
    icon          blob,
    primary key (registry_name, nbt)
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
    fluid_registry_name text,
    fluid_nbt text,
    slot   integer not null,
    amount integer,
    primary key (fluid_registry_name, gregtech_recipe_id, slot),
    foreign key (fluid_registry_name, fluid_nbt) references fluid (registry_name, nbt)
) without rowid;

create table gregtech_recipe_input_item
(
    gregtech_recipe_id references gregtech_recipe,
    item_registry_name text,
    item_nbt text,
    slot   integer not null,
    amount integer,
    primary key (gregtech_recipe_id, item_registry_name, slot),
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt)
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
    fluid_registry_name text,
    fluid_nbt text,
    slot   integer not null,
    amount integer,
    primary key (fluid_registry_name, gregtech_recipe_id, slot),
    foreign key (fluid_registry_name, fluid_nbt) references fluid (registry_name, nbt)
) without rowid;

create table gregtech_recipe_output_item
(
    gregtech_recipe_id references gregtech_recipe,
    item_registry_name text,
    item_nbt text,
    slot   integer not null,
    amount integer,
    chance integer,
    primary key (gregtech_recipe_id, item_registry_name, slot),
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt)
) without rowid;

create table item
(
    registry_name text,
    display_name  text,
    nbt           text,
    icon          blob,
    primary key (registry_name, nbt)
) without rowid;

create table ore
(
    name text not null primary key
) without rowid;

create table ore_item
(
    item_registry_name text,
    item_nbt text,
    ore_name references ore,
    primary key (ore_name, item_registry_name),
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt)
) without rowid;

create table crafting_table_recipe
(
    id integer primary key,
    shaped integer,
    output_item_registry_name text,
    output_item_nbt text,
    foreign key (output_item_registry_name, output_item_nbt) references item (registry_name, nbt)
);

create table crafting_table_recipe_input_item
(
    item_registry_name text,
    item_nbt text,
    crafting_table_recipe_id references crafting_table_recipe,
    slot integer not null,
    primary key (item_registry_name, crafting_table_recipe_id, slot),
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt)
) without rowid;

create table crafting_table_recipe_input_ore
(
    ore_name references ore,
    crafting_table_recipe_id references crafting_table_recipe,
    slot integer not null,
    primary key (ore_name, crafting_table_recipe_id, slot)
) without rowid;

create table aspect
(
    tag  text primary key,
    name text,
    icon blob
) without rowid;
