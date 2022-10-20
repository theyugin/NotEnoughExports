create table item
(
    unlocalizedName text primary key,
    localizedName   text,
    icon            blob
) without rowid;
create table fluid
(
    unlocalizedName text primary key,
    localizedName   text
) without rowid;
create table ore
(
    name text primary key
) without rowid;
create table oreItem
(
    item text references item,
    name text references ore,
    primary key (item, name)
) without rowid;
create table catalystType
(
    name text primary key
) without rowid;
create table catalystTypeItem
(
    name text references catalystType,
    item text references item,
    primary key (name, item)
) without rowid;
create table shapedRecipe
(
    id     integer primary key,
    output text references item
);
create table shapedRecipeInputItem
(
    recipe integer references shapedRecipe,
    item   text references item,
    slot   int,
    primary key (recipe, item, slot)
) without rowid;
create table shapedRecipeInputOre
(
    recipe integer references shapedRecipe,
    ore    text references ore,
    slot   int,
    primary key (recipe, ore, slot)
) without rowid;
create table shapelessRecipe
(
    id     integer primary key,
    output text references item
);
create table shapelessRecipeInputItem
(
    recipe integer references shapelessRecipe,
    item   text references item,
    primary key (recipe, item)
) without rowid;
create table shapelessRecipeInputOre
(
    recipe integer references shapelessRecipe,
    ore    text references ore,
    primary key (recipe, ore)
) without rowid;
create table gregtechRecipe
(
    id       integer primary key,
    voltage  integer,
    amperage integer,
    duration integer,
    config   integer,
    catalyst text references catalystType
);
create table gregtechRecipeInputItem
(
    recipe integer references gregtechRecipe,
    item   text references item,
    slot   integer,
    amount integer,
    primary key (recipe, item, slot)
) without rowid;
create table gregtechRecipeInputFluid
(
    recipe integer references gregtechRecipe,
    fluid  text references fluid,
    slot   integer,
    amount integer,
    primary key (recipe, fluid, slot)
) without rowid;
create table gregtechRecipeInputOre
(
    recipe integer references gregtechRecipe,
    ore    text references ore,
    slot   integer,
    amount integer,
    primary key (recipe, ore, slot)
) without rowid;
create table gregtechRecipeOutputItem
(
    recipe integer references gregtechRecipe,
    item   text references item,
    slot   integer,
    amount integer,
    chance integer,
    primary key (recipe, item, slot)
) without rowid;
create table gregtechRecipeOutputFluid
(
    recipe integer references gregtechRecipe,
    fluid  text references fluid,
    slot   integer,
    amount integer,
    primary key (recipe, fluid, slot)
) without rowid;
