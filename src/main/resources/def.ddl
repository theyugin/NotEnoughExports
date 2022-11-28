create table item
(
    registry_name text,
    display_name  text,
    nbt           text,
    icon          blob,
    primary key (registry_name, nbt)
) without rowid;

create table fluid
(
    registry_name text,
    display_name  text,
    nbt           text,
    icon          blob,
    primary key (registry_name, nbt)
) without rowid;

create table aspect
(
    tag  text primary key,
    name text,
    icon blob
) without rowid;

create table ore
(
    name text not null primary key
) without rowid;

create table catalyst
(
    name text primary key
) without rowid;

create table catalyst_item
(
    catalyst_name references catalyst,
    item_registry_name text,
    item_nbt           text,
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt),
    primary key (catalyst_name, item_registry_name)
) without rowid;

create table ore_item
(
    ore_name references ore,
    item_registry_name text,
    item_nbt           text,
    primary key (ore_name, item_registry_name, item_nbt),
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt)
) without rowid;

create table recipe
(
    id         integer primary key,
    catalyst_name references catalyst,
    info_table text
);

create table recipe_input_fluid
(
    recipe_id references recipe,
    fluid_registry_name text,
    fluid_nbt           text,
    slot                integer not null,
    amount              integer,
    primary key (recipe_id, fluid_registry_name, fluid_nbt, slot),
    foreign key (fluid_registry_name, fluid_nbt) references fluid (registry_name, nbt)
) without rowid;

create table recipe_input_item
(
    recipe_id references recipe,
    item_registry_name text,
    item_nbt           text,
    slot               integer not null,
    amount             integer,
    primary key (recipe_id, item_registry_name, item_nbt, slot),
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt)
) without rowid;

create table recipe_input_ore
(
    recipe_id references recipe,
    ore_name references ore,
    slot   integer not null,
    amount integer,
    primary key (recipe_id, ore_name, slot)
) without rowid;

create table recipe_output_fluid
(
    recipe_id references recipe,
    fluid_registry_name text,
    fluid_nbt           text,
    slot                integer not null,
    amount              integer,
    primary key (recipe_id, fluid_registry_name, fluid_nbt, slot),
    foreign key (fluid_registry_name, fluid_nbt) references fluid (registry_name, nbt)
) without rowid;

create table recipe_output_item
(
    recipe_id references recipe,
    item_registry_name text,
    item_nbt           text,
    slot               integer not null,
    amount             integer,
    chance             integer,
    primary key (recipe_id, item_registry_name, item_nbt, slot),
    foreign key (item_registry_name, item_nbt) references item (registry_name, nbt)
) without rowid;

create table recipe_input_aspect
(
    recipe_id references recipe,
    aspect_tag references aspect,
    amount integer,
    primary key (recipe_id, aspect_tag, amount)
) without rowid;

create table crafting_table_recipe
(
    id references recipe primary key,
    shaped integer
);

create table gregtech_recipe
(
    id references recipe primary key,
    amperage integer,
    config   integer,
    duration integer,
    voltage  integer
);

create table gregtech_fuel_recipe
(
    id references recipe primary key,
    fuel_value      integer,
    fuel_multiplier integer
);

create table thaumcraft_infusion_recipe
(
    id references recipe primary key,
    instability int,
    research    text
);

create table thaumcraft_arcane_recipe
(
    id references recipe primary key,
    shaped integer
);
