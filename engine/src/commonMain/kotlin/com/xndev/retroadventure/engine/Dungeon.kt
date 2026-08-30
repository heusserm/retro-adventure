// Generated from vendor/open-adventure/adventure.yaml by scripts/gen_dungeon.py - do not hand-hack!
// Upstream data is (C) Eric S. Raymond, BSD-2-Clause; see vendor/open-adventure/COPYING.

@file:Suppress("unused", "ObjectPropertyName", "SpellCheckingInspection")

package com.xndev.retroadventure.engine

// ---------------------------------------------------------------------------
// Table shapes. These mirror the structs in upstream templates/dungeon.h.tpl.
// ---------------------------------------------------------------------------

data class Location(
    val small: String?,
    val big: String?,
    val sound: Int,
    val loud: Boolean,
)

data class Obj(
    val words: List<String>,
    val inventory: String?,
    val plac: Int,
    val fixd: Int,
    val isTreasure: Boolean,
    val descriptions: List<String>,
    val sounds: List<String>,
    val texts: List<String>,
    val changes: List<String>,
)

data class Obituary(val query: String, val yesResponse: String)

data class TurnThreshold(val threshold: Int, val pointLoss: Int, val message: String)

data class ClassMsg(val threshold: Int, val message: String?)

data class Hint(
    val number: Int,
    val penalty: Int,
    val turns: Int,
    val question: String,
    val hint: String,
)

data class Motion(val words: List<String>)

data class Action(val words: List<String>, val message: String?, val noAction: Boolean)

enum class CondType { GOTO, PCT, CARRY, WITH, NOT }

enum class DestType { GOTO, SPECIAL, SPEAK }

data class TravelOp(
    val motion: Int,
    val condType: CondType,
    val condArg1: Int,
    val condArg2: Int,
    val destType: DestType,
    val destVal: Int,
    val noDwarves: Boolean,
    val stop: Boolean,
)

// Symbols for cond bits, from upstream templates/dungeon.h.tpl
const val COND_LIT = 0           // Light
const val COND_OILY = 1          // If bit 2 is on: on for oil, off for water
const val COND_FLUID = 2         // Liquid asset, see bit 1
const val COND_NOARRR = 3        // Pirate doesn't go here unless following
const val COND_NOBACK = 4        // Cannot use "back" to move away
const val COND_ABOVE = 5         // Aboveground, but not in forest
const val COND_DEEP = 6          // Deep - e.g. where dwarves are active
const val COND_FOREST = 7        // In the forest
const val COND_FORCED = 8        // Only one way in or out of here
const val COND_ALLDIFFERENT = 9  // Room is in maze all different
const val COND_ALLALIKE = 10     // Room is in maze all alike

// Bits past 11 indicate areas of interest to "hint" routines
const val COND_HBASE = 11        // Base for location hint bits
const val COND_HCAVE = 12        // Trying to get into cave
const val COND_HBIRD = 13        // Trying to catch bird
const val COND_HSNAKE = 14       // Trying to deal with snake
const val COND_HMAZE = 15        // Lost in maze
const val COND_HDARK = 16        // Pondering dark room
const val COND_HWITT = 17        // At Witt's End
const val COND_HCLIFF = 18       // Cliff with urn
const val COND_HWOODS = 19       // Lost in forest
const val COND_HOGRE = 20        // Trying to deal with ogre
const val COND_HJADE = 21        // Found all treasures except jade

const val SILENT = -1            // no sound

// Table sizes
const val NLOCATIONS = 185
const val NOBJECTS = 70
const val NHINTS = 10
const val NCLASSES = 11
const val NDEATHS = 3
const val NTHRESHOLDS = 4
const val NMOTIONS = 76
const val NACTIONS = 58
const val NTRAVEL = 878
const val NKEYS = 185
const val NDWARVES = 6
const val BIRD_ENDSTATE = 5

// Single-letter words the parser must ignore, from `oldstyle: false` entries
const val IGNORE = "LXGZI"

// Arbitrary message refs
const val NO_MESSAGE = 0
const val CAVE_NEARBY = 1
const val DWARF_BLOCK = 2
const val DWARF_RAN = 3
const val DWARF_PACK = 4
const val DWARF_SINGLE = 5
const val KNIFE_THROWN = 6
const val SAYS_PLUGH = 7
const val GETS_YOU = 8
const val MISSES_YOU = 9
const val UNSURE_FACING = 10
const val NO_INOUT_HERE = 11
const val CANT_APPLY = 12
const val AM_GAME = 13
const val NO_MORE_DETAIL = 14
const val PITCH_DARK = 15
const val W_IS_WEST = 16
const val REALLY_QUIT = 17
const val PIT_FALL = 18
const val ALREADY_CARRYING = 19
const val YOU_JOKING = 20
const val BIRD_EVADES = 21
const val CANNOT_CARRY = 22
const val NOTHING_LOCKED = 23
const val ARENT_CARRYING = 24
const val BIRD_ATTACKS = 25
const val NO_KEYS = 26
const val NO_LOCK = 27
const val NOT_LOCKABLE = 28
const val ALREADY_LOCKED = 29
const val ALREADY_UNLOCKED = 30
const val BEAR_BLOCKS = 31
const val NOTHING_HAPPENS = 32
const val WHERE_QUERY = 33
const val NO_TARGET = 34
const val BIRD_DEAD = 35
const val SNAKE_WARNING = 36
const val KILLED_DWARF = 37
const val DWARF_DODGES = 38
const val BARE_HANDS_QUERY = 39
const val BAD_DIRECTION = 40
const val TWO_WORDS = 41
const val OK_MAN = 42
const val CANNOT_UNLOCK = 43
const val FUTILE_CRAWL = 44
const val FOLLOW_STREAM = 45
const val NEED_DETAIL = 46
const val NEARBY = 47
const val OGRE_SNARL = 48
const val HUH_MAN = 49
const val WELCOME_YOU = 50
const val REQUIRES_DYNAMITE = 51
const val FEET_WET = 52
const val LOST_APPETITE = 53
const val THANKS_DELICIOUS = 54
const val PECULIAR_NOTHING = 55
const val GROUND_WET = 56
const val CANT_POUR = 57
const val WHICH_WAY = 58
const val FORGOT_PATH = 59
const val CARRY_LIMIT = 60
const val GRATE_NOWAY = 61
const val YOU_HAVEIT = 62
const val DONT_FIT = 63
const val CROSS_BRIDGE = 64
const val NO_CROSS = 65
const val NO_CARRY = 66
const val NOW_HOLDING = 67
const val BIRD_PINING = 68
const val BIRD_DEVOURED = 69
const val NOTHING_EDIBLE = 70
const val REALLY_MAD = 71
const val NO_CONTAINER = 72
const val BOTTLE_FULL = 73
const val NO_LIQUID = 74
const val RIDICULOUS_ATTEMPT = 75
const val RUSTY_DOOR = 76
const val SHAKING_LEAVES = 77
const val DEEP_ROOTS = 78
const val KNIVES_VANISH = 79
const val MUST_DROP = 80
const val CLAM_BLOCKER = 81
const val OYSTER_BLOCKER = 82
const val DROP_CLAM = 83
const val DROP_OYSTER = 84
const val CLAM_OPENER = 85
const val OYSTER_OPENER = 86
const val PEARL_FALLS = 87
const val OYSTER_OPENS = 88
const val WAY_BLOCKED = 89
const val PIRATE_RUSTLES = 90
const val PIRATE_POUNCES = 91
const val CAVE_CLOSING = 92
const val EXIT_CLOSED = 93
const val DEATH_CLOSING = 94
const val CAVE_CLOSED = 95
const val VICTORY_MESSAGE = 96
const val DEFEAT_MESSAGE = 97
const val SPLATTER_MESSAGE = 98
const val DWARVES_AWAKEN = 99
const val UNHAPPY_BIRD = 100
const val NEEDED_NEARBY = 101
const val NOT_CONNECTED = 102
const val TAME_BEAR = 103
const val WITHOUT_SUSPENDS = 104
const val FILL_INVALID = 105
const val SHATTER_VASE = 106
const val BEYOND_POWER = 107
const val NOT_KNOWHOW = 108
const val TOO_FAR = 109
const val DWARF_SMOKE = 110
const val SHELL_IMPERVIOUS = 111
const val START_OVER = 112
const val WELL_POINTLESS = 113
const val DRAGON_SCALES = 114
const val NASTY_DRAGON = 115
const val BIRD_BURNT = 116
const val BRIEF_CONFIRM = 117
const val ROCKY_TROLL = 118
const val TROLL_RETURNS = 119
const val TROLL_SATISFIED = 120
const val TROLL_BLOCKS = 121
const val BRIDGE_GONE = 122
const val BEAR_HANDS = 123
const val BEAR_CONFUSED = 124
const val ALREADY_DEAD = 125
const val BEAR_CHAINED = 126
const val STILL_LOCKED = 127
const val CHAIN_UNLOCKED = 128
const val CHAIN_LOCKED = 129
const val NO_LOCKSITE = 130
const val WANT_HINT = 131
const val TROLL_VICES = 132
const val LAMP_DIM = 133
const val LAMP_OUT = 134
const val PLEASE_ANSWER = 135
const val PIRATE_SPOTTED = 136
const val GET_BATTERIES = 137
const val REPLACE_BATTERIES = 138
const val MISSING_BATTERIES = 139
const val REMOVE_MESSAGE = 140
const val CLUE_QUERY = 141
const val WAYOUT_CLUE = 142
const val DONT_UNDERSTAND = 143
const val HAND_PASSTHROUGH = 144
const val PROD_DWARF = 145
const val THIS_ACCEPTABLE = 146
const val OGRE_FULL = 147
const val OGRE_DODGE = 148
const val OGRE_PANIC1 = 149
const val OGRE_PANIC2 = 150
const val FREE_FLY = 151
const val CAGE_FLY = 152
const val NECKLACE_FLY = 153
const val WATER_URN = 154
const val OIL_URN = 155
const val FULL_URN = 156
const val URN_NOPOUR = 157
const val URN_NOBUDGE = 158
const val URN_GENIES = 159
const val DOUGHNUT_HOLES = 160
const val GEM_FITS = 161
const val RUG_RISES = 162
const val RUG_WIGGLES = 163
const val RUG_SETTLES = 164
const val RUG_HOVERS = 165
const val RUG_NOTHING1 = 166
const val RUG_NOTHING2 = 167
const val FLAP_ARMS = 168
const val RUG_GOES = 169
const val RUG_RETURNS = 170
const val ALL_SILENT = 171
const val STREAM_GURGLES = 172
const val WIND_WHISTLES = 173
const val STREAM_SPLASHES = 174
const val NO_MEANING = 175
const val MURMURING_SNORING = 176
const val SNAKES_HISSING = 177
const val DULL_RUMBLING = 178
const val LOUD_ROAR = 179
const val TOTAL_ROAR = 180
const val BIRD_CRAP = 181
const val FEW_DROPS = 182
const val NOT_BRIGHT = 183
const val TOOK_LONG = 184
const val UPSTREAM_DOWNSTREAM = 185
const val FOREST_QUERY = 186
const val WATERS_CRASHING = 187
const val THROWN_KNIVES = 188
const val MULTIPLE_HITS = 189
const val ONE_HIT = 190
const val NONE_HIT = 191
const val DONT_KNOW = 192
const val WHAT_DO = 193
const val NO_SEE = 194
const val DO_WHAT = 195
const val OKEY_DOKEY = 196
const val GARNERED_POINTS = 197
const val SUSPEND_WARNING = 198
const val HINT_COST = 199
const val TOTAL_SCORE = 200
const val NEXT_HIGHER = 201
const val NO_HIGHER = 202
const val OFF_SCALE = 203
const val SAVERESUME_DISABLED = 204
const val RESUME_HELP = 205
const val RESUME_ABANDON = 206
const val BAD_SAVE = 207
const val VERSION_SKEW = 208
const val SAVE_TAMPERING = 209
const val TWIST_TURN = 210
const val GO_UNNEEDED = 211
const val NUMERIC_REQUIRED = 212

// Location refs
const val LOC_NOWHERE = 0
const val LOC_START = 1
const val LOC_HILL = 2
const val LOC_BUILDING = 3
const val LOC_VALLEY = 4
const val LOC_ROADEND = 5
const val LOC_CLIFF = 6
const val LOC_SLIT = 7
const val LOC_GRATE = 8
const val LOC_BELOWGRATE = 9
const val LOC_COBBLE = 10
const val LOC_DEBRIS = 11
const val LOC_AWKWARD = 12
const val LOC_BIRDCHAMBER = 13
const val LOC_PITTOP = 14
const val LOC_MISTHALL = 15
const val LOC_CRACK = 16
const val LOC_EASTBANK = 17
const val LOC_NUGGET = 18
const val LOC_KINGHALL = 19
const val LOC_NECKBROKE = 20
const val LOC_NOMAKE = 21
const val LOC_DOME = 22
const val LOC_WESTEND = 23
const val LOC_EASTPIT = 24
const val LOC_WESTPIT = 25
const val LOC_CLIMBSTALK = 26
const val LOC_WESTBANK = 27
const val LOC_FLOORHOLE = 28
const val LOC_SOUTHSIDE = 29
const val LOC_WESTSIDE = 30
const val LOC_BUILDING1 = 31
const val LOC_SNAKEBLOCK = 32
const val LOC_Y2 = 33
const val LOC_JUMBLE = 34
const val LOC_WINDOW1 = 35
const val LOC_BROKEN = 36
const val LOC_SMALLPITBRINK = 37
const val LOC_SMALLPIT = 38
const val LOC_DUSTY = 39
const val LOC_PARALLEL1 = 40
const val LOC_MISTWEST = 41
const val LOC_ALIKE1 = 42
const val LOC_ALIKE2 = 43
const val LOC_ALIKE3 = 44
const val LOC_ALIKE4 = 45
const val LOC_MAZEEND1 = 46
const val LOC_MAZEEND2 = 47
const val LOC_MAZEEND3 = 48
const val LOC_ALIKE5 = 49
const val LOC_ALIKE6 = 50
const val LOC_ALIKE7 = 51
const val LOC_ALIKE8 = 52
const val LOC_ALIKE9 = 53
const val LOC_MAZEEND4 = 54
const val LOC_ALIKE10 = 55
const val LOC_MAZEEND5 = 56
const val LOC_PITBRINK = 57
const val LOC_MAZEEND6 = 58
const val LOC_PARALLEL2 = 59
const val LOC_LONGEAST = 60
const val LOC_LONGWEST = 61
const val LOC_CROSSOVER = 62
const val LOC_DEADEND7 = 63
const val LOC_COMPLEX = 64
const val LOC_BEDQUILT = 65
const val LOC_SWISSCHEESE = 66
const val LOC_EASTEND = 67
const val LOC_SLAB = 68
const val LOC_SECRET1 = 69
const val LOC_SECRET2 = 70
const val LOC_THREEJUNCTION = 71
const val LOC_LOWROOM = 72
const val LOC_DEADCRAWL = 73
const val LOC_SECRET3 = 74
const val LOC_WIDEPLACE = 75
const val LOC_TIGHTPLACE = 76
const val LOC_TALL = 77
const val LOC_BOULDERS1 = 78
const val LOC_SEWER = 79
const val LOC_ALIKE11 = 80
const val LOC_MAZEEND8 = 81
const val LOC_MAZEEND9 = 82
const val LOC_ALIKE12 = 83
const val LOC_ALIKE13 = 84
const val LOC_MAZEEND10 = 85
const val LOC_MAZEEND11 = 86
const val LOC_ALIKE14 = 87
const val LOC_NARROW = 88
const val LOC_NOCLIMB = 89
const val LOC_PLANTTOP = 90
const val LOC_INCLINE = 91
const val LOC_GIANTROOM = 92
const val LOC_CAVEIN = 93
const val LOC_IMMENSE = 94
const val LOC_WATERFALL = 95
const val LOC_SOFTROOM = 96
const val LOC_ORIENTAL = 97
const val LOC_MISTY = 98
const val LOC_ALCOVE = 99
const val LOC_PLOVER = 100
const val LOC_DARKROOM = 101
const val LOC_ARCHED = 102
const val LOC_SHELLROOM = 103
const val LOC_SLOPING1 = 104
const val LOC_CULDESAC = 105
const val LOC_ANTEROOM = 106
const val LOC_DIFFERENT1 = 107
const val LOC_WITTSEND = 108
const val LOC_MIRRORCANYON = 109
const val LOC_WINDOW2 = 110
const val LOC_TOPSTALACTITE = 111
const val LOC_DIFFERENT2 = 112
const val LOC_RESERVOIR = 113
const val LOC_MAZEEND12 = 114
const val LOC_NE = 115
const val LOC_SW = 116
const val LOC_SWCHASM = 117
const val LOC_WINDING = 118
const val LOC_SECRET4 = 119
const val LOC_SECRET5 = 120
const val LOC_SECRET6 = 121
const val LOC_NECHASM = 122
const val LOC_CORRIDOR = 123
const val LOC_FORK = 124
const val LOC_WARMWALLS = 125
const val LOC_BREATHTAKING = 126
const val LOC_BOULDERS2 = 127
const val LOC_LIMESTONE = 128
const val LOC_BARRENFRONT = 129
const val LOC_BARRENROOM = 130
const val LOC_DIFFERENT3 = 131
const val LOC_DIFFERENT4 = 132
const val LOC_DIFFERENT5 = 133
const val LOC_DIFFERENT6 = 134
const val LOC_DIFFERENT7 = 135
const val LOC_DIFFERENT8 = 136
const val LOC_DIFFERENT9 = 137
const val LOC_DIFFERENT10 = 138
const val LOC_DIFFERENT11 = 139
const val LOC_DEADEND13 = 140
const val LOC_ROUGHHEWN = 141
const val LOC_BADDIRECTION = 142
const val LOC_LARGE = 143
const val LOC_STOREROOM = 144
const val LOC_FOREST1 = 145
const val LOC_FOREST2 = 146
const val LOC_FOREST3 = 147
const val LOC_FOREST4 = 148
const val LOC_FOREST5 = 149
const val LOC_FOREST6 = 150
const val LOC_FOREST7 = 151
const val LOC_FOREST8 = 152
const val LOC_FOREST9 = 153
const val LOC_FOREST10 = 154
const val LOC_FOREST11 = 155
const val LOC_FOREST12 = 156
const val LOC_FOREST13 = 157
const val LOC_FOREST14 = 158
const val LOC_FOREST15 = 159
const val LOC_FOREST16 = 160
const val LOC_FOREST17 = 161
const val LOC_FOREST18 = 162
const val LOC_FOREST19 = 163
const val LOC_FOREST20 = 164
const val LOC_FOREST21 = 165
const val LOC_FOREST22 = 166
const val LOC_LEDGE = 167
const val LOC_RESBOTTOM = 168
const val LOC_RESNORTH = 169
const val LOC_TREACHEROUS = 170
const val LOC_STEEP = 171
const val LOC_CLIFFBASE = 172
const val LOC_CLIFFACE = 173
const val LOC_FOOTSLIP = 174
const val LOC_CLIFFTOP = 175
const val LOC_CLIFFLEDGE = 176
const val LOC_REACHDEAD = 177
const val LOC_GRUESOME = 178
const val LOC_FOOF1 = 179
const val LOC_FOOF2 = 180
const val LOC_FOOF3 = 181
const val LOC_FOOF4 = 182
const val LOC_FOOF5 = 183
const val LOC_FOOF6 = 184

// Object refs
const val NO_OBJECT = 0
const val KEYS = 1
const val LAMP = 2
const val GRATE = 3
const val CAGE = 4
const val ROD = 5
const val ROD2 = 6
const val STEPS = 7
const val BIRD = 8
const val DOOR = 9
const val PILLOW = 10
const val SNAKE = 11
const val FISSURE = 12
const val OBJ_13 = 13
const val CLAM = 14
const val OYSTER = 15
const val MAGAZINE = 16
const val DWARF = 17
const val KNIFE = 18
const val FOOD = 19
const val BOTTLE = 20
const val WATER = 21
const val OIL = 22
const val MIRROR = 23
const val PLANT = 24
const val PLANT2 = 25
const val OBJ_26 = 26
const val OBJ_27 = 27
const val AXE = 28
const val OBJ_29 = 29
const val OBJ_30 = 30
const val DRAGON = 31
const val CHASM = 32
const val TROLL = 33
const val TROLL2 = 34
const val BEAR = 35
const val MESSAG = 36
const val VOLCANO = 37
const val VEND = 38
const val BATTERY = 39
const val OBJ_40 = 40
const val OGRE = 41
const val URN = 42
const val CAVITY = 43
const val BLOOD = 44
const val RESER = 45
const val RABBITFOOT = 46
const val OBJ_47 = 47
const val OBJ_48 = 48
const val SIGN = 49
const val NUGGET = 50
const val OBJ_51 = 51
const val OBJ_52 = 52
const val OBJ_53 = 53
const val COINS = 54
const val CHEST = 55
const val EGGS = 56
const val TRIDENT = 57
const val VASE = 58
const val EMERALD = 59
const val PYRAMID = 60
const val PEARL = 61
const val RUG = 62
const val OBJ_63 = 63
const val CHAIN = 64
const val RUBY = 65
const val JADE = 66
const val AMBER = 67
const val SAPPH = 68
const val OBJ_69 = 69

// Motion refs
const val MOT_0 = 0
const val HERE = 1
const val MOT_2 = 2
const val ENTER = 3
const val MOT_4 = 4
const val MOT_5 = 5
const val MOT_6 = 6
const val FORWARD = 7
const val BACK = 8
const val MOT_9 = 9
const val MOT_10 = 10
const val OUTSIDE = 11
const val MOT_12 = 12
const val MOT_13 = 13
const val STREAM = 14
const val MOT_15 = 15
const val MOT_16 = 16
const val CRAWL = 17
const val MOT_18 = 18
const val INSIDE = 19
const val MOT_20 = 20
const val NUL = 21
const val MOT_22 = 22
const val MOT_23 = 23
const val MOT_24 = 24
const val MOT_25 = 25
const val MOT_26 = 26
const val MOT_27 = 27
const val MOT_28 = 28
const val UP = 29
const val DOWN = 30
const val MOT_31 = 31
const val MOT_32 = 32
const val MOT_33 = 33
const val MOT_34 = 34
const val MOT_35 = 35
const val LEFT = 36
const val RIGHT = 37
const val MOT_38 = 38
const val MOT_39 = 39
const val MOT_40 = 40
const val MOT_41 = 41
const val MOT_42 = 42
const val EAST = 43
const val WEST = 44
const val NORTH = 45
const val SOUTH = 46
const val NE = 47
const val SE = 48
const val SW = 49
const val NW = 50
const val MOT_51 = 51
const val MOT_52 = 52
const val MOT_53 = 53
const val MOT_54 = 54
const val MOT_55 = 55
const val MOT_56 = 56
const val LOOK = 57
const val MOT_58 = 58
const val MOT_59 = 59
const val MOT_60 = 60
const val MOT_61 = 61
const val XYZZY = 62
const val DEPRESSION = 63
const val ENTRANCE = 64
const val PLUGH = 65
const val MOT_66 = 66
const val CAVE = 67
const val CROSS = 68
const val BEDQUILT = 69
const val PLOVER = 70
const val ORIENTAL = 71
const val CAVERN = 72
const val SHELLROOM = 73
const val RESERVOIR = 74
const val OFFICE = 75

// Action refs
const val ACT_NULL = 0
const val CARRY = 1
const val DROP = 2
const val SAY = 3
const val UNLOCK = 4
const val NOTHING = 5
const val LOCK = 6
const val LIGHT = 7
const val EXTINGUISH = 8
const val WAVE = 9
const val TAME = 10
const val GO = 11
const val ATTACK = 12
const val POUR = 13
const val EAT = 14
const val DRINK = 15
const val RUB = 16
const val THROW = 17
const val QUIT = 18
const val FIND = 19
const val INVENTORY = 20
const val FEED = 21
const val FILL = 22
const val BLAST = 23
const val SCORE = 24
const val FEE = 25
const val FIE = 26
const val FOE = 27
const val FOO = 28
const val FUM = 29
const val BRIEF = 30
const val READ = 31
const val BREAK = 32
const val WAKE = 33
const val SAVE = 34
const val RESUME = 35
const val FLY = 36
const val LISTEN = 37
const val PART = 38
const val SEED = 39
const val WASTE = 40
const val ACT_UNKNOWN = 41
const val THANKYOU = 42
const val INVALIDMAGIC = 43
const val HELP = 44
const val False = 45
const val TREE = 46
const val DIG = 47
const val LOST = 48
const val MIST = 49
const val FBOMB = 50
const val STOP = 51
const val INFO = 52
const val SWIM = 53
const val WIZARD = 54
const val YES = 55
const val NEWS = 56
const val ACT_VERSION = 57

// Object state definitions
// States for LAMP
const val LAMP_DARK = 0
const val LAMP_BRIGHT = 1

// States for GRATE
const val GRATE_CLOSED = 0
const val GRATE_OPEN = 1

// States for STEPS
const val STEPS_DOWN = 0
const val STEPS_UP = 1

// States for BIRD
const val BIRD_UNCAGED = 0
const val BIRD_CAGED = 1
const val BIRD_FOREST_UNCAGED = 2

// States for DOOR
const val DOOR_RUSTED = 0
const val DOOR_UNRUSTED = 1

// States for SNAKE
const val SNAKE_BLOCKS = 0
const val SNAKE_CHASED = 1

// States for FISSURE
const val UNBRIDGED = 0
const val BRIDGED = 1

// States for BOTTLE
const val WATER_BOTTLE = 0
const val EMPTY_BOTTLE = 1
const val OIL_BOTTLE = 2

// States for MIRROR
const val MIRROR_UNBROKEN = 0
const val MIRROR_BROKEN = 1

// States for PLANT
const val PLANT_THIRSTY = 0
const val PLANT_BELLOWING = 1
const val PLANT_GROWN = 2

// States for AXE
const val AXE_HERE = 0
const val AXE_LOST = 1

// States for DRAGON
const val DRAGON_BARS = 0
const val DRAGON_DEAD = 1
const val DRAGON_BLOODLESS = 2

// States for CHASM
const val TROLL_BRIDGE = 0
const val BRIDGE_WRECKED = 1

// States for TROLL
const val TROLL_UNPAID = 0
const val TROLL_PAIDONCE = 1
const val TROLL_GONE = 2

// States for BEAR
const val UNTAMED_BEAR = 0
const val SITTING_BEAR = 1
const val CONTENTED_BEAR = 2
const val BEAR_DEAD = 3

// States for VEND
const val VEND_BLOCKS = 0
const val VEND_UNBLOCKS = 1

// States for BATTERY
const val FRESH_BATTERIES = 0
const val DEAD_BATTERIES = 1

// States for URN
const val URN_EMPTY = 0
const val URN_DARK = 1
const val URN_LIT = 2

// States for CAVITY
const val CAVITY_FULL = 0
const val CAVITY_EMPTY = 1

// States for RESER
const val WATERS_UNPARTED = 0
const val WATERS_PARTED = 1

// States for SIGN
const val INGAME_SIGN = 0
const val ENDGAME_SIGN = 1

// States for EGGS
const val EGGS_HERE = 0
const val EGGS_VANISHED = 1
const val EGGS_DONE = 2

// States for VASE
const val VASE_WHOLE = 0
const val VASE_DROPPED = 1
const val VASE_BROKEN = 2

// States for RUG
const val RUG_FLOOR = 0
const val RUG_DRAGON = 1
const val RUG_HOVER = 2

// States for CHAIN
const val CHAIN_HEAP = 0
const val CHAINING_BEAR = 1
const val CHAIN_FIXED = 2

// States for AMBER
const val AMBER_IN_URN = 0
const val AMBER_IN_ROCK = 1

// Maximum state value
const val MAX_STATE = 3

val dwarflocs: IntArray = intArrayOf(LOC_KINGHALL, LOC_WESTBANK, LOC_Y2, LOC_ALIKE3, LOC_COMPLEX, LOC_MAZEEND12)

val arbitraryMessages: Array<String?> = arrayOf(
    null, // NO_MESSAGE
    "Somewhere nearby is Colossal Cave, where others have found fortunes in\ntreasure and gold, though it is rumored that some who enter are never\nseen again.  Magic is said to work in the cave.  I will be your eyes\nand hands.  Direct me with commands of 1 or 2 words.  I should warn\nyou that I look at only the first five letters of each word, so you'll\nhave to enter \"northeast\" as \"ne\" to distinguish it from \"north\".\nYou can type \"help\" for some general hints.  For information on how\nto end your adventure, scoring, etc., type \"info\".\n\t\t\t      - - -\nThis program was originally developed by Willie Crowther.  Most of the\nfeatures of the current program were added by Don Woods.\nPorting to Mobile was led by Matthew Heusser.", // CAVE_NEARBY
    "A little dwarf with a big knife blocks your way.", // DWARF_BLOCK
    "A little dwarf just walked around a corner, saw you, threw a little\naxe at you which missed, cursed, and ran away.", // DWARF_RAN
    "There are %d threatening little dwarves in the room with you.", // DWARF_PACK
    "There is a threatening little dwarf in the room with you!", // DWARF_SINGLE
    "One sharp nasty knife is thrown at you!", // KNIFE_THROWN
    "A hollow voice says \"PLUGH\".", // SAYS_PLUGH
    "It gets you!", // GETS_YOU
    "It misses!", // MISSES_YOU
    "I am unsure how you are facing.  Use compass points or nearby objects.", // UNSURE_FACING
    "I don't know in from out here.  Use compass points or name something\nin the general direction you want to go.", // NO_INOUT_HERE
    "I don't know how to apply that word here.", // CANT_APPLY
    "I'm game.  Would you care to explain how?", // AM_GAME
    "Sorry, but I am not allowed to give more detail.  I will repeat the\nlong description of your location.", // NO_MORE_DETAIL
    "It is now pitch dark.  If you proceed you will likely fall into a pit.", // PITCH_DARK
    "If you prefer, simply type w rather than west.", // W_IS_WEST
    "Do you really want to quit now?", // REALLY_QUIT
    "You fell into a pit and broke every bone in your body!", // PIT_FALL
    "You are already carrying it!", // ALREADY_CARRYING
    "You can't be serious!", // YOU_JOKING
    "The bird seemed unafraid at first, but as you approach it becomes\ndisturbed and you cannot catch it.", // BIRD_EVADES
    "You can catch the bird, but you cannot carry it.", // CANNOT_CARRY
    "There is nothing here with a lock!", // NOTHING_LOCKED
    "You aren't carrying it!", // ARENT_CARRYING
    "The little bird attacks the green snake, and in an astounding flurry\ndrives the snake away.", // BIRD_ATTACKS
    "You have no keys!", // NO_KEYS
    "It has no lock.", // NO_LOCK
    "I don't know how to lock or unlock such a thing.", // NOT_LOCKABLE
    "It was already locked.", // ALREADY_LOCKED
    "It was already unlocked.", // ALREADY_UNLOCKED
    "There is no way to get past the bear to unlock the chain, which is\nprobably just as well.", // BEAR_BLOCKS
    "Nothing happens.", // NOTHING_HAPPENS
    "Where?", // WHERE_QUERY
    "There is nothing here to attack.", // NO_TARGET
    "The little bird is now dead.  Its body disappears.", // BIRD_DEAD
    "Attacking the snake both doesn't work and is very dangerous.", // SNAKE_WARNING
    "You killed a little dwarf.", // KILLED_DWARF
    "You attack a little dwarf, but he dodges out of the way.", // DWARF_DODGES
    "With what?  Your bare hands?", // BARE_HANDS_QUERY
    "There is no way to go that direction.", // BAD_DIRECTION
    "Please stick to 1- and 2-word commands.", // TWO_WORDS
    "OK", // OK_MAN
    "You can't unlock the keys.", // CANNOT_UNLOCK
    "You have crawled around in some little holes and wound up back in the\nmain passage.", // FUTILE_CRAWL
    "I don't know where the cave is, but hereabouts no stream can run on\nthe surface for long.  I would try the stream.", // FOLLOW_STREAM
    "I need more detailed instructions to do that.", // NEED_DETAIL
    "I can only tell you what you see as you move about and manipulate\nthings.  I cannot tell you where remote things are.", // NEARBY
    "The ogre snarls and shoves you back.", // OGRE_SNARL
    "Huh?", // HUH_MAN
    "Welcome to Adventure!!  Would you like instructions?", // WELCOME_YOU
    "Blasting requires dynamite.", // REQUIRES_DYNAMITE
    "Your feet are now wet.", // FEET_WET
    "I think I just lost my appetite.", // LOST_APPETITE
    "Thank you, it was delicious!", // THANKS_DELICIOUS
    "Peculiar.  Nothing unexpected happens.", // PECULIAR_NOTHING
    "Your bottle is empty and the ground is wet.", // GROUND_WET
    "You can't pour that.", // CANT_POUR
    "Which way?", // WHICH_WAY
    "Sorry, but I no longer seem to remember how it was you got here.", // FORGOT_PATH
    "You can't carry anything more.  You'll have to drop something first.", // CARRY_LIMIT
    "You can't go through a locked steel grate!", // GRATE_NOWAY
    "I believe what you want is right here with you.", // YOU_HAVEIT
    "You don't fit through a two-inch slit!", // DONT_FIT
    "I respectfully suggest you go across the bridge instead of jumping.", // CROSS_BRIDGE
    "There is no way across the fissure.", // NO_CROSS
    "You're not carrying anything.", // NO_CARRY
    "You are currently holding the following:", // NOW_HOLDING
    "It's not hungry (it's merely pinin' for the fjords).  Besides, you\nhave no bird seed.", // BIRD_PINING
    "The snake has now devoured your bird.", // BIRD_DEVOURED
    "There's nothing here it wants to eat (except perhaps you).", // NOTHING_EDIBLE
    "You fool, dwarves eat only coal!  Now you've made him *REALLY* mad!!", // REALLY_MAD
    "You have nothing in which to carry it.", // NO_CONTAINER
    "Your bottle is already full.", // BOTTLE_FULL
    "There is nothing here with which to fill the bottle.", // NO_LIQUID
    "Don't be ridiculous!", // RIDICULOUS_ATTEMPT
    "The door is extremely rusty and refuses to open.", // RUSTY_DOOR
    "The plant indignantly shakes the oil off its leaves and asks, \"Water?\"", // SHAKING_LEAVES
    "The plant has exceptionally deep roots and cannot be pulled free.", // DEEP_ROOTS
    "The dwarves' knives vanish as they strike the walls of the cave.", // KNIVES_VANISH
    "Something you're carrying won't fit through the tunnel with you.\nYou'd best take inventory and drop something.", // MUST_DROP
    "You can't fit this five-foot clam through that little passage!", // CLAM_BLOCKER
    "You can't fit this five-foot oyster through that little passage!", // OYSTER_BLOCKER
    "I advise you to put down the clam before opening it.  >STRAIN!<", // DROP_CLAM
    "I advise you to put down the oyster before opening it.  >WRENCH!<", // DROP_OYSTER
    "You don't have anything strong enough to open the clam.", // CLAM_OPENER
    "You don't have anything strong enough to open the oyster.", // OYSTER_OPENER
    "A glistening pearl falls out of the clam and rolls away.  Goodness,\nthis must really be an oyster.  (I never was very good at identifying\nbivalves.)  Whatever it is, it has now snapped shut again.", // PEARL_FALLS
    "The oyster creaks open, revealing nothing but oyster inside.  It\npromptly snaps shut again.", // OYSTER_OPENS
    "You have crawled around in some little holes and found your way\nblocked by a recent cave-in.  You are now back in the main passage.", // WAY_BLOCKED
    "There are faint rustling noises from the darkness behind you.", // PIRATE_RUSTLES
    "Out from the shadows behind you pounces a bearded pirate!  \"Har, har,\"\nhe chortles, \"I'll just take all this booty and hide it away with me\nchest deep in the maze!\"  He snatches your treasure and vanishes into\nthe gloom.", // PIRATE_POUNCES
    "A sepulchral voice reverberating through the cave, says, \"Cave closing\nsoon.  All adventurers exit immediately through main office.\"", // CAVE_CLOSING
    "A mysterious recorded voice groans into life and announces:\n   \"This exit is closed.  Please leave via main office.\"", // EXIT_CLOSED
    "It looks as though you're dead.  Well, seeing as how it's so close to\nclosing time anyway, I think we'll just call it a day.", // DEATH_CLOSING
    "The sepulchral voice intones, \"The cave is now closed.\"  As the echoes\nfade, there is a blinding flash of light (and a small puff of orange\nsmoke). . . .    As your eyes refocus, you look around and find...", // CAVE_CLOSED
    "There is a loud explosion, and a twenty-foot hole appears in the far\nwall, burying the dwarves in the rubble.  You march through the hole\nand find yourself in the main office, where a cheering band of\nfriendly elves carry the conquering adventurer off into the sunset.", // VICTORY_MESSAGE
    "There is a loud explosion, and a twenty-foot hole appears in the far\nwall, burying the snakes in the rubble.  A river of molten lava pours\nin through the hole, destroying everything in its path, including you!", // DEFEAT_MESSAGE
    "There is a loud explosion, and you are suddenly splashed across the\nwalls of the room.", // SPLATTER_MESSAGE
    "The resulting ruckus has awakened the dwarves.  There are now several\nthreatening little dwarves in the room with you!  Most of them throw\nknives at you!  All of them get you!", // DWARVES_AWAKEN
    "Oh, leave the poor unhappy bird alone.", // UNHAPPY_BIRD
    "I daresay whatever you want is around here somewhere.", // NEEDED_NEARBY
    "You can't get there from here.", // NOT_CONNECTED
    "You are being followed by a very large, tame bear.", // TAME_BEAR
    "Now let's see you do it without suspending in mid-Adventure.", // WITHOUT_SUSPENDS
    "There is nothing here with which to fill it.", // FILL_INVALID
    "The sudden change in temperature has delicately shattered the vase.", // SHATTER_VASE
    "It is beyond your power to do that.", // BEYOND_POWER
    "I don't know how.", // NOT_KNOWHOW
    "It is too far up for you to reach.", // TOO_FAR
    "You killed a little dwarf.  The body vanishes in a cloud of greasy\nblack smoke.", // DWARF_SMOKE
    "The shell is very strong and is impervious to attack.", // SHELL_IMPERVIOUS
    "What's the matter, can't you read?  Now you'd best start over.", // START_OVER
    "Well, that was remarkably pointless!", // WELL_POINTLESS
    "The axe bounces harmlessly off the dragon's thick scales.", // DRAGON_SCALES
    "The dragon looks rather nasty.  You'd best not try to get by.", // NASTY_DRAGON
    "The little bird attacks the green dragon, and in an astounding flurry\ngets burnt to a cinder.  The ashes blow away.", // BIRD_BURNT
    "Okay, from now on I'll only describe a place in full the first time\nyou come to it.  To get the full description, say \"look\".", // BRIEF_CONFIRM
    "Trolls are close relatives with the rocks and have skin as tough as\nthat of a rhinoceros.  The troll fends off your blows effortlessly.", // ROCKY_TROLL
    "The troll deftly catches the axe, examines it carefully, and tosses it\nback, declaring, \"Good workmanship, but it's not valuable enough.\"", // TROLL_RETURNS
    "The troll catches your treasure and scurries away out of sight.", // TROLL_SATISFIED
    "The troll refuses to let you cross.", // TROLL_BLOCKS
    "There is no longer any way across the chasm.", // BRIDGE_GONE
    "With what?  Your bare hands?  Against *HIS* bear hands??", // BEAR_HANDS
    "The bear is confused; he only wants to be your friend.", // BEAR_CONFUSED
    "For crying out loud, the poor thing is already dead!", // ALREADY_DEAD
    "The bear is still chained to the wall.", // BEAR_CHAINED
    "The chain is still locked.", // STILL_LOCKED
    "The chain is now unlocked.", // CHAIN_UNLOCKED
    "The chain is now locked.", // CHAIN_LOCKED
    "There is nothing here to which the chain can be locked.", // NO_LOCKSITE
    "Do you want the hint?", // WANT_HINT
    "Gluttony is not one of the troll's vices.  Avarice, however, is.", // TROLL_VICES
    "Your lamp is getting dim.  You'd best start wrapping this up, unless\nyou can find some fresh batteries.  I seem to recall there's a vending\nmachine in the maze.  Bring some coins with you.", // LAMP_DIM
    "Your lamp has run out of power.", // LAMP_OUT
    "Please answer the question.", // PLEASE_ANSWER
    "There are faint rustling noises from the darkness behind you.  As you\nturn toward them, the beam of your lamp falls across a bearded pirate.\nHe is carrying a large chest.  \"Shiver me timbers!\" he cries, \"I've\nbeen spotted!  I'd best hie meself off to the maze to hide me chest!\"\nWith that, he vanishes into the gloom.", // PIRATE_SPOTTED
    "Your lamp is getting dim.  You'd best go back for those batteries.", // GET_BATTERIES
    "Your lamp is getting dim.  I'm taking the liberty of replacing the\nbatteries.", // REPLACE_BATTERIES
    "Your lamp is getting dim, and you're out of spare batteries.  You'd\nbest start wrapping this up.", // MISSING_BATTERIES
    "You sift your fingers through the dust, but succeed only in\nobliterating the cryptic message.", // REMOVE_MESSAGE
    "Hmmm, this looks like a clue, which means it'll cost you 10 points to\nread it.  Should I go ahead and read it anyway?", // CLUE_QUERY
    "It says, \"There is a way out of this place.  Do you need any more\ninformation to escape?  Sorry, but this initial hint is all you get.\"", // WAYOUT_CLUE
    "I'm afraid I don't understand.", // DONT_UNDERSTAND
    "Your hand passes through it as though it weren't there.", // HAND_PASSTHROUGH
    "You prod the nearest dwarf, who wakes up grumpily, takes one look at\nyou, curses, and grabs for his axe.", // PROD_DWARF
    "Is this acceptable?", // THIS_ACCEPTABLE
    "The ogre doesn't appear to be hungry.", // OGRE_FULL
    "The ogre, who despite his bulk is quite agile, easily dodges your\nattack.  He seems almost amused by your puny effort.", // OGRE_DODGE
    "The ogre, distracted by your rush, is struck by the knife.  With a\nblood-curdling yell he turns and bounds after the dwarves, who flee\nin panic.  You are left alone in the room.", // OGRE_PANIC1
    "The ogre, distracted by your rush, is struck by the knife.  With a\nblood-curdling yell he turns and bounds after the dwarf, who flees\nin panic.  You are left alone in the room.", // OGRE_PANIC2
    "The bird flies about agitatedly for a moment.", // FREE_FLY
    "The bird flies agitatedly about the cage.", // CAGE_FLY
    "The bird flies about agitatedly for a moment, then disappears through\nthe crack.  It reappears shortly, carrying in its beak a jade\nnecklace, which it drops at your feet.", // NECKLACE_FLY
    "You empty the bottle into the urn, which promptly ejects the water\nwith uncanny accuracy, squirting you directly between the eyes.", // WATER_URN
    "Your bottle is now empty and the urn is full of oil.", // OIL_URN
    "The urn is already full of oil.", // FULL_URN
    "There's no way to get the oil out of the urn.", // URN_NOPOUR
    "The urn is far too firmly embedded for your puny strength to budge it.", // URN_NOBUDGE
    "As you rub the urn, there is a flash of light and a genie appears.\nHis aspect is stern as he advises: \"One who wouldst traffic in\nprecious stones must first learn to recognize the signals thereof.\"\nHe wrests the urn from the stone, leaving a small cavity.  Turning to\nface you again, he fixes you with a steely eye and intones: \"Caution!\"\nGenie and urn vanish in a cloud of amber smoke.  The smoke condenses\nto form a rare amber gemstone, resting in the cavity in the rock.", // URN_GENIES
    "I suppose you collect doughnut holes, too?", // DOUGHNUT_HOLES
    "The gem fits easily into the cavity.", // GEM_FITS
    "The Persian rug stiffens and rises a foot or so off the ground.", // RUG_RISES
    "The Persian rug draped over your shoulder seems to wriggle for a\nmoment, but then subsides.", // RUG_WIGGLES
    "The Persian rug settles gently to the ground.", // RUG_SETTLES
    "The rug hovers stubbornly where it is.", // RUG_HOVERS
    "The rug does not appear inclined to cooperate.", // RUG_NOTHING1
    "If you mean to use the Persian rug, it does not appear inclined to\ncooperate.", // RUG_NOTHING2
    "Though you flap your arms furiously, it is to no avail.", // FLAP_ARMS
    "You board the Persian rug, which promptly whisks you across the chasm.\nYou have time for a fleeting glimpse of a two thousand foot drop to a\nmighty river; then you find yourself on the other side.", // RUG_GOES
    "The rug ferries you back across the chasm.", // RUG_RETURNS
    "All is silent.", // ALL_SILENT
    "The stream is gurgling placidly.", // STREAM_GURGLES
    "The wind whistles coldly past your ears.", // WIND_WHISTLES
    "The stream splashes loudly into the pool.", // STREAM_SPLASHES
    "You are unable to make anything of the splashing noise.", // NO_MEANING
    "You can hear the murmuring of the beanstalks and the snoring of the\ndwarves.", // MURMURING_SNORING
    "A loud hissing emanates from the snake pit.", // SNAKES_HISSING
    "The air is filled with a dull rumbling sound.", // DULL_RUMBLING
    "The roar is quite loud here.", // LOUD_ROAR
    "The roaring is so loud that it drowns out all other sound.", // TOTAL_ROAR
    "The bird eyes you suspiciously and flutters away.  A moment later you\nfeel something wet land on your head, but upon looking up you can see\nno sign of the culprit.", // BIRD_CRAP
    "There are only a few drops--not enough to carry.", // FEW_DROPS
    "(Uh, y'know, that wasn't very bright.)", // NOT_BRIGHT
    "It's a pity you took so long about it.", // TOOK_LONG
    "Upstream or downstream?", // UPSTREAM_DOWNSTREAM
    null, // FOREST_QUERY
    "The waters are crashing loudly against the shore.", // WATERS_CRASHING
    "%d of them throw knives at you!", // THROWN_KNIVES
    "%d of them get you!", // MULTIPLE_HITS
    "One of them gets you!", // ONE_HIT
    "None of them hits you!", // NONE_HIT
    "Sorry, I don't know the word \"%s\".", // DONT_KNOW
    "What do you want to do with the %s?", // WHAT_DO
    "I see no %s here.", // NO_SEE
    "%s what?", // DO_WHAT
    "Okay, \"%s\".", // OKEY_DOKEY
    "You have garnered %d out of a possible %d points, using %d turn%S.", // GARNERED_POINTS
    "I can suspend your Adventure for you so that you can resume later, but\nit will cost you 5 points.", // SUSPEND_WARNING
    "I am prepared to give you a hint, but it will cost you %d point%S.", // HINT_COST
    "You scored %d out of a possible %d, using %d turn%S.", // TOTAL_SCORE
    "To achieve the next higher rating, you need %d more point%S.", // NEXT_HIGHER
    "To achieve the next higher rating would be a neat trick!\nCongratulations!!", // NO_HIGHER
    "You just went off my scale!!", // OFF_SCALE
    "Save and resume are disabled.", // SAVERESUME_DISABLED
    "To resume your Adventure, start a new game and then say \"RESUME\".", // RESUME_HELP
    "To resume an earlier Adventure, you must abandon the current one.", // RESUME_ABANDON
    "Oops, that does not look like a valid save file.", // BAD_SAVE
    "I'm sorry, but that Adventure was begun using Version %d.%d of the\nsave file format, and this program uses Version %d.%d.  You must find an instance\nusing that other version in order to resume that Adventure.", // VERSION_SKEW
    "A dark fog creeps in to surround you.  From somewhere in the fog you\nhear a stern voice.  \"This Adventure has been tampered with!  You have\nbeen dabbling in magic, knowing not the havoc you might cause thereby.\nLeave at once, before you do irrevocable harm!\"  The fog thickens,\nuntil at last you can see nothing at all.  Your vision then clears,\nand you find yourself back in The Real World.", // SAVE_TAMPERING
    "Sorry, but the path twisted and turned so much that I can't figure\nout which way to go to get back.", // TWIST_TURN
    "You don't have to say \"go\" every time; just specify a direction or, if\nit's nearby, name the place to which you wish to move.", // GO_UNNEEDED
    "This command requires a numeric argument.", // NUMERIC_REQUIRED
)

val locations: Array<Location> = arrayOf(
    Location(small = null, big = null, sound = SILENT, loud = false), // 0: LOC_NOWHERE
    Location(small = "You're in front of building.", big = "You are standing at the end of a road before a small brick building.\nAround you is a forest.  A small stream flows out of the building and\ndown a gully.", sound = STREAM_GURGLES, loud = false), // 1: LOC_START
    Location(small = "You're at hill in road.", big = "You have walked up a hill, still in the forest.  The road slopes back\ndown the other side of the hill.  There is a building in the distance.", sound = SILENT, loud = false), // 2: LOC_HILL
    Location(small = "You're inside building.", big = "You are inside a building, a well house for a large spring.", sound = STREAM_GURGLES, loud = false), // 3: LOC_BUILDING
    Location(small = "You're in valley.", big = "You are in a valley in the forest beside a stream tumbling along a\nrocky bed.", sound = STREAM_GURGLES, loud = false), // 4: LOC_VALLEY
    Location(small = "You're at end of road.", big = "The road, which approaches from the east, ends here amid the trees.", sound = SILENT, loud = false), // 5: LOC_ROADEND
    Location(small = "You're at cliff.", big = "The forest thins out here to reveal a steep cliff.  There is no way\ndown, but a small ledge can be seen to the west across the chasm.", sound = SILENT, loud = false), // 6: LOC_CLIFF
    Location(small = "You're at slit in streambed.", big = "At your feet all the water of the stream splashes into a 2-inch slit\nin the rock.  Downstream the streambed is bare rock.", sound = STREAM_GURGLES, loud = false), // 7: LOC_SLIT
    Location(small = "You're outside grate.", big = "You are in a 20-foot depression floored with bare dirt.  Set into the\ndirt is a strong steel grate mounted in concrete.  A dry streambed\nleads into the depression.", sound = SILENT, loud = false), // 8: LOC_GRATE
    Location(small = "You're below the grate.", big = "You are in a small chamber beneath a 3x3 steel grate to the surface.\nA low crawl over cobbles leads inward to the west.", sound = SILENT, loud = false), // 9: LOC_BELOWGRATE
    Location(small = "You're in cobble crawl.", big = "You are crawling over cobbles in a low passage.  There is a dim light\nat the east end of the passage.", sound = SILENT, loud = false), // 10: LOC_COBBLE
    Location(small = "You're in debris room.", big = "You are in a debris room filled with stuff washed in from the surface.\nA low wide passage with cobbles becomes plugged with mud and debris\nhere, but an awkward canyon leads upward and west.  In the mud someone\nhas scrawled, \"MAGIC WORD XYZZY\".", sound = SILENT, loud = false), // 11: LOC_DEBRIS
    Location(small = null, big = "You are in an awkward sloping east/west canyon.", sound = SILENT, loud = false), // 12: LOC_AWKWARD
    Location(small = "You're in bird chamber.", big = "You are in a splendid chamber thirty feet high.  The walls are frozen\nrivers of orange stone.  An awkward canyon and a good passage exit\nfrom east and west sides of the chamber.", sound = SILENT, loud = false), // 13: LOC_BIRDCHAMBER
    Location(small = "You're at top of small pit.", big = "At your feet is a small pit breathing traces of white mist.  An east\npassage ends here except for a small crack leading on.", sound = SILENT, loud = false), // 14: LOC_PITTOP
    Location(small = "You're in Hall of Mists.", big = "You are at one end of a vast hall stretching forward out of sight to\nthe west.  There are openings to either side.  Nearby, a wide stone\nstaircase leads downward.  The hall is filled with wisps of white mist\nswaying to and fro almost as if alive.  A cold wind blows up the\nstaircase.  There is a passage at the top of a dome behind you.", sound = WIND_WHISTLES, loud = false), // 15: LOC_MISTHALL
    Location(small = null, big = "The crack is far too small for you to follow.  At its widest it is\nbarely wide enough to admit your foot.", sound = SILENT, loud = false), // 16: LOC_CRACK
    Location(small = "You're on east bank of fissure.", big = "You are on the east bank of a fissure slicing clear across the hall.\nThe mist is quite thick here, and the fissure is too wide to jump.", sound = SILENT, loud = false), // 17: LOC_EASTBANK
    Location(small = "You're in nugget-of-gold room.", big = "This is a low room with a crude note on the wall.  The note says,\n\"You won't get it up the steps\".", sound = SILENT, loud = false), // 18: LOC_NUGGET
    Location(small = "You're in Hall of Mt King.", big = "You are in the Hall of the Mountain King, with passages off in all\ndirections.", sound = SILENT, loud = false), // 19: LOC_KINGHALL
    Location(small = null, big = "You are at the bottom of the pit with a broken neck.", sound = SILENT, loud = false), // 20: LOC_NECKBROKE
    Location(small = null, big = "You didn't make it.", sound = SILENT, loud = false), // 21: LOC_NOMAKE
    Location(small = null, big = "The dome is unclimbable.", sound = SILENT, loud = false), // 22: LOC_DOME
    Location(small = "You're at west end of Twopit Room.", big = "You are at the west end of the Twopit Room.  There is a large hole in\nthe wall above the pit at this end of the room.", sound = SILENT, loud = false), // 23: LOC_WESTEND
    Location(small = "You're in east pit.", big = "You are at the bottom of the eastern pit in the Twopit Room.  There is\na small pool of oil in one corner of the pit.", sound = SILENT, loud = false), // 24: LOC_EASTPIT
    Location(small = "You're in west pit.", big = "You are at the bottom of the western pit in the Twopit Room.  There is\na large hole in the wall about 25 feet above you.", sound = SILENT, loud = false), // 25: LOC_WESTPIT
    Location(small = null, big = "You clamber up the plant and scurry through the hole at the top.", sound = SILENT, loud = false), // 26: LOC_CLIMBSTALK
    Location(small = "You're on west bank of fissure.", big = "You are on the west side of the fissure in the Hall of Mists.", sound = SILENT, loud = false), // 27: LOC_WESTBANK
    Location(small = "You're in n/s passage above e/w passage.", big = "You are in a low n/s passage at a hole in the floor.  The hole goes\ndown to an e/w passage.", sound = SILENT, loud = false), // 28: LOC_FLOORHOLE
    Location(small = null, big = "You are in the south side chamber.", sound = SILENT, loud = false), // 29: LOC_SOUTHSIDE
    Location(small = "You're in the west side chamber.", big = "You are in the west side chamber of the Hall of the Mountain King.\nA passage continues west and up here.", sound = SILENT, loud = false), // 30: LOC_WESTSIDE
    Location(small = null, big = "", sound = SILENT, loud = false), // 31: LOC_BUILDING1
    Location(small = null, big = "You can't get by the snake.", sound = SILENT, loud = false), // 32: LOC_SNAKEBLOCK
    Location(small = "You're at \"Y2\".", big = "You are in a large room, with a passage to the south, a passage to the\nwest, and a wall of broken rock to the east.  There is a large \"Y2\" on\na rock in the room's center.", sound = SILENT, loud = false), // 33: LOC_Y2
    Location(small = null, big = "You are in a jumble of rock, with cracks everywhere.", sound = SILENT, loud = false), // 34: LOC_JUMBLE
    Location(small = "You're at window on pit.", big = "You're at a low window overlooking a huge pit, which extends up out of\nsight.  A floor is indistinctly visible over 50 feet below.  Traces of\nwhite mist cover the floor of the pit, becoming thicker to the right.\nMarks in the dust around the window would seem to indicate that\nsomeone has been here recently.  Directly across the pit from you and\n25 feet away there is a similar window looking into a lighted room.  A\nshadowy figure can be seen there peering back at you.", sound = SILENT, loud = false), // 35: LOC_WINDOW1
    Location(small = "You're in dirty passage.", big = "You are in a dirty broken passage.  To the east is a crawl.  To the\nwest is a large passage.  Above you is a hole to another passage.", sound = SILENT, loud = false), // 36: LOC_BROKEN
    Location(small = "You're at brink of small pit.", big = "You are on the brink of a small clean climbable pit.  A crawl leads\nwest.", sound = SILENT, loud = false), // 37: LOC_SMALLPITBRINK
    Location(small = "You're at bottom of pit with stream.", big = "You are in the bottom of a small pit with a little stream, which\nenters and exits through tiny slits.", sound = STREAM_GURGLES, loud = false), // 38: LOC_SMALLPIT
    Location(small = "You're in dusty rock room.", big = "You are in a large room full of dusty rocks.  There is a big hole in\nthe floor.  There are cracks everywhere, and a passage leading east.", sound = SILENT, loud = false), // 39: LOC_DUSTY
    Location(small = null, big = "You have crawled through a very low wide passage parallel to and north\nof the Hall of Mists.", sound = SILENT, loud = false), // 40: LOC_PARALLEL1
    Location(small = "You're at west end of Hall of Mists.", big = "You are at the west end of the Hall of Mists.  A low wide crawl\ncontinues west and another goes north.  To the south is a little\npassage 6 feet off the floor.", sound = SILENT, loud = false), // 41: LOC_MISTWEST
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 42: LOC_ALIKE1
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 43: LOC_ALIKE2
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 44: LOC_ALIKE3
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 45: LOC_ALIKE4
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 46: LOC_MAZEEND1
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 47: LOC_MAZEEND2
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 48: LOC_MAZEEND3
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 49: LOC_ALIKE5
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 50: LOC_ALIKE6
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 51: LOC_ALIKE7
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 52: LOC_ALIKE8
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 53: LOC_ALIKE9
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 54: LOC_MAZEEND4
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 55: LOC_ALIKE10
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 56: LOC_MAZEEND5
    Location(small = "You're at brink of pit.", big = "You are on the brink of a thirty foot pit with a massive orange column\ndown one wall.  You could climb down here but you could not get back\nup.  The maze continues at this level.", sound = SILENT, loud = false), // 57: LOC_PITBRINK
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 58: LOC_MAZEEND6
    Location(small = null, big = "You have crawled through a very low wide passage parallel to and north\nof the Hall of Mists.", sound = SILENT, loud = false), // 59: LOC_PARALLEL2
    Location(small = "You're at east end of long hall.", big = "You are at the east end of a very long hall apparently without side\nchambers.  To the east a low wide crawl slants up.  To the north a\nround two foot hole slants down.", sound = SILENT, loud = false), // 60: LOC_LONGEAST
    Location(small = "You're at west end of long hall.", big = "You are at the west end of a very long featureless hall.  The hall\njoins up with a narrow north/south passage.", sound = SILENT, loud = false), // 61: LOC_LONGWEST
    Location(small = null, big = "You are at a crossover of a high n/s passage and a low e/w one.", sound = SILENT, loud = false), // 62: LOC_CROSSOVER
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 63: LOC_DEADEND7
    Location(small = "You're at complex junction.", big = "You are at a complex junction.  A low hands and knees passage from the\nnorth joins a higher crawl from the east to make a walking passage\ngoing west.  There is also a large room above.  The air is damp here.", sound = WIND_WHISTLES, loud = false), // 64: LOC_COMPLEX
    Location(small = "You're in Bedquilt.", big = "You are in Bedquilt, a long east/west passage with holes everywhere.\nTo explore at random select north, south, up, or down.", sound = SILENT, loud = false), // 65: LOC_BEDQUILT
    Location(small = "You're in Swiss Cheese Room.", big = "You are in a room whose walls resemble Swiss cheese.  Obvious passages\ngo west, east, ne, and nw.  Part of the room is occupied by a large\nbedrock block.", sound = SILENT, loud = false), // 66: LOC_SWISSCHEESE
    Location(small = "You're at east end of Twopit Room.", big = "You are at the east end of the Twopit Room.  The floor here is\nlittered with thin rock slabs, which make it easy to descend the pits.\nThere is a path here bypassing the pits to connect passages from east\nand west.  There are holes all over, but the only big one is on the\nwall directly over the west pit where you can't get to it.", sound = SILENT, loud = false), // 67: LOC_EASTEND
    Location(small = "You're in Slab Room.", big = "You are in a large low circular chamber whose floor is an immense slab\nfallen from the ceiling (Slab Room).  East and west there once were\nlarge passages, but they are now filled with boulders.  Low small\npassages go north and south, and the south one quickly bends west\naround the boulders.", sound = SILENT, loud = false), // 68: LOC_SLAB
    Location(small = null, big = "You are in a secret n/s canyon above a large room.", sound = SILENT, loud = false), // 69: LOC_SECRET1
    Location(small = null, big = "You are in a secret n/s canyon above a sizable passage.", sound = SILENT, loud = false), // 70: LOC_SECRET2
    Location(small = "You're at junction of three secret canyons.", big = "You are in a secret canyon at a junction of three canyons, bearing\nnorth, south, and se.  The north one is as tall as the other two\ncombined.", sound = SILENT, loud = false), // 71: LOC_THREEJUNCTION
    Location(small = "You're in large low room.", big = "You are in a large low room.  Crawls lead north, se, and sw.", sound = SILENT, loud = false), // 72: LOC_LOWROOM
    Location(small = null, big = "Dead end crawl.", sound = SILENT, loud = false), // 73: LOC_DEADCRAWL
    Location(small = "You're in secret e/w canyon above tight canyon.", big = "You are in a secret canyon which here runs e/w.  It crosses over a\nvery tight canyon 15 feet below.  If you go down you may not be able\nto get back up.", sound = SILENT, loud = false), // 74: LOC_SECRET3
    Location(small = null, big = "You are at a wide place in a very tight n/s canyon.", sound = SILENT, loud = false), // 75: LOC_WIDEPLACE
    Location(small = null, big = "The canyon here becomes too tight to go further south.", sound = SILENT, loud = false), // 76: LOC_TIGHTPLACE
    Location(small = null, big = "You are in a tall e/w canyon.  A low tight crawl goes 3 feet north and\nseems to open up.", sound = SILENT, loud = false), // 77: LOC_TALL
    Location(small = null, big = "The canyon runs into a mass of boulders -- dead end.", sound = SILENT, loud = false), // 78: LOC_BOULDERS1
    Location(small = null, big = "The stream flows out through a pair of 1 foot diameter sewer pipes.\nIt would be advisable to use the exit.", sound = SILENT, loud = false), // 79: LOC_SEWER
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 80: LOC_ALIKE11
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 81: LOC_MAZEEND8
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 82: LOC_MAZEEND9
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 83: LOC_ALIKE12
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 84: LOC_ALIKE13
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 85: LOC_MAZEEND10
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 86: LOC_MAZEEND11
    Location(small = null, big = "You are in a maze of twisty little passages, all alike.", sound = SILENT, loud = false), // 87: LOC_ALIKE14
    Location(small = "You're in narrow corridor.", big = "You are in a long, narrow corridor stretching out of sight to the\nwest.  At the eastern end is a hole through which you can see a\nprofusion of leaves.", sound = SILENT, loud = false), // 88: LOC_NARROW
    Location(small = null, big = "There is nothing here to climb.  Use \"up\" or \"out\" to leave the pit.", sound = SILENT, loud = false), // 89: LOC_NOCLIMB
    Location(small = null, big = "You have climbed up the plant and out of the pit.", sound = SILENT, loud = false), // 90: LOC_PLANTTOP
    Location(small = "You're at steep incline above large room.", big = "You are at the top of a steep incline above a large room.  You could\nclimb down here, but you would not be able to climb up.  There is a\npassage leading back to the north.", sound = SILENT, loud = false), // 91: LOC_INCLINE
    Location(small = "You're in Giant Room.", big = "You are in the Giant Room.  The ceiling here is too high up for your\nlamp to show it.  Cavernous passages lead east, north, and south.  On\nthe west wall is scrawled the inscription, \"FEE FIE FOE FOO\" [sic].", sound = SILENT, loud = false), // 92: LOC_GIANTROOM
    Location(small = null, big = "The passage here is blocked by a recent cave-in.", sound = SILENT, loud = false), // 93: LOC_CAVEIN
    Location(small = null, big = "You are at one end of an immense north/south passage.", sound = WIND_WHISTLES, loud = false), // 94: LOC_IMMENSE
    Location(small = "You're in cavern with waterfall.", big = "You are in a magnificent cavern with a rushing stream, which cascades\nover a sparkling waterfall into a roaring whirlpool which disappears\nthrough a hole in the floor.  Passages exit to the south and west.", sound = STREAM_SPLASHES, loud = false), // 95: LOC_WATERFALL
    Location(small = "You're in Soft Room.", big = "You are in the Soft Room.  The walls are covered with heavy curtains,\nthe floor with a thick pile carpet.  Moss covers the ceiling.", sound = SILENT, loud = false), // 96: LOC_SOFTROOM
    Location(small = "You're in Oriental Room.", big = "This is the Oriental Room.  Ancient oriental cave drawings cover the\nwalls.  A gently sloping passage leads upward to the north, another\npassage leads se, and a hands and knees crawl leads west.", sound = SILENT, loud = false), // 97: LOC_ORIENTAL
    Location(small = "You're in misty cavern.", big = "You are following a wide path around the outer edge of a large cavern.\nFar below, through a heavy white mist, strange splashing noises can be\nheard.  The mist rises up through a fissure in the ceiling.  The path\nexits to the south and west.", sound = NO_MEANING, loud = false), // 98: LOC_MISTY
    Location(small = "You're in alcove.", big = "You are in an alcove.  A small nw path seems to widen after a short\ndistance.  An extremely tight tunnel leads east.  It looks like a very\ntight squeeze.  An eerie light can be seen at the other end.", sound = SILENT, loud = false), // 99: LOC_ALCOVE
    Location(small = "You're in Plover Room.", big = "You're in a small chamber lit by an eerie green light.  An extremely\nnarrow tunnel exits to the west.  A dark corridor leads ne.", sound = SILENT, loud = false), // 100: LOC_PLOVER
    Location(small = "You're in dark-room.", big = "You're in the dark-room.  A corridor leading south is the only exit.", sound = SILENT, loud = false), // 101: LOC_DARKROOM
    Location(small = "You're in arched hall.", big = "You are in an arched hall.  A coral passage once continued up and east\nfrom here, but is now blocked by debris.  The air smells of sea water.", sound = SILENT, loud = false), // 102: LOC_ARCHED
    Location(small = "You're in Shell Room.", big = "You're in a large room carved out of sedimentary rock.  The floor and\nwalls are littered with bits of shells embedded in the stone.  A\nshallow passage proceeds downward, and a somewhat steeper one leads\nup.  A low hands and knees passage enters from the south.", sound = SILENT, loud = false), // 103: LOC_SHELLROOM
    Location(small = null, big = "You are in a long sloping corridor with ragged sharp walls.", sound = SILENT, loud = false), // 104: LOC_SLOPING1
    Location(small = null, big = "You are in a cul-de-sac about eight feet across.", sound = SILENT, loud = false), // 105: LOC_CULDESAC
    Location(small = "You're in anteroom.", big = "You are in an anteroom leading to a large passage to the east.  Small\npassages go west and up.  The remnants of recent digging are evident.\nA sign in midair here says \"Cave under construction beyond this point.\nProceed at own risk.  [Witt Construction Company]\"", sound = SILENT, loud = false), // 106: LOC_ANTEROOM
    Location(small = null, big = "You are in a maze of twisty little passages, all different.", sound = SILENT, loud = false), // 107: LOC_DIFFERENT1
    Location(small = "You're at Witt's End.", big = "You are at Witt's End.  Passages lead off in *ALL* directions.", sound = SILENT, loud = false), // 108: LOC_WITTSEND
    Location(small = "You're in Mirror Canyon.", big = "You are in a north/south canyon about 25 feet across.  The floor is\ncovered by white mist seeping in from the north.  The walls extend\nupward for well over 100 feet.  Suspended from some unseen point far\nabove you, an enormous two-sided mirror is hanging parallel to and\nmidway between the canyon walls.  (The mirror is obviously provided\nfor the use of the dwarves who, as you know, are extremely vain.)  A\nsmall window can be seen in either wall, some fifty feet up.", sound = WIND_WHISTLES, loud = false), // 109: LOC_MIRRORCANYON
    Location(small = "You're at window on pit.", big = "You're at a low window overlooking a huge pit, which extends up out of\nsight.  A floor is indistinctly visible over 50 feet below.  Traces of\nwhite mist cover the floor of the pit, becoming thicker to the left.\nMarks in the dust around the window would seem to indicate that\nsomeone has been here recently.  Directly across the pit from you and\n25 feet away there is a similar window looking into a lighted room.  A\nshadowy figure can be seen there peering back at you.", sound = SILENT, loud = false), // 110: LOC_WINDOW2
    Location(small = "You're at top of stalactite.", big = "A large stalactite extends from the roof and almost reaches the floor\nbelow.  You could climb down it, and jump from it to the floor, but\nhaving done so you would be unable to reach it to climb back up.", sound = SILENT, loud = false), // 111: LOC_TOPSTALACTITE
    Location(small = null, big = "You are in a little maze of twisting passages, all different.", sound = SILENT, loud = false), // 112: LOC_DIFFERENT2
    Location(small = "You're at reservoir.", big = "You are at the edge of a large underground reservoir.  An opaque cloud\nof white mist fills the room and rises rapidly upward.  The lake is\nfed by a stream, which tumbles out of a hole in the wall about 10 feet\noverhead and splashes noisily into the water somewhere within the\nmist.  There is a passage going back toward the south.", sound = STREAM_SPLASHES, loud = false), // 113: LOC_RESERVOIR
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 114: LOC_MAZEEND12
    Location(small = "You're at ne end.", big = "You are at the northeast end of an immense room, even larger than the\nGiant Room.  It appears to be a repository for the \"Adventure\"\nprogram.  Massive torches far overhead bathe the room with smoky\nyellow light.  Scattered about you can be seen a pile of bottles (all\nof them empty), a nursery of young beanstalks murmuring quietly, a bed\nof oysters, a bundle of black rods with rusty stars on their ends, and\na collection of brass lanterns.  Off to one side a great many dwarves\nare sleeping on the floor, snoring loudly.  A notice nearby reads: \"Do\nnot disturb the dwarves!\"  An immense mirror is hanging against one\nwall, and stretches to the other end of the room, where various other\nsundry objects can be glimpsed dimly in the distance.", sound = MURMURING_SNORING, loud = false), // 115: LOC_NE
    Location(small = "You're at sw end.", big = "You are at the southwest end of the repository.  To one side is a pit\nfull of fierce green snakes.  On the other side is a row of small\nwicker cages, each of which contains a little sulking bird.  In one\ncorner is a bundle of black rods with rusty marks on their ends.  A\nlarge number of velvet pillows are scattered about on the floor.  A\nvast mirror stretches off to the northeast.  At your feet is a large\nsteel grate, next to which is a sign that reads, \"Treasure Vault.\nKeys in main office.\"", sound = SNAKES_HISSING, loud = false), // 116: LOC_SW
    Location(small = "You're on sw side of chasm.", big = "You are on one side of a large, deep chasm.  A heavy white mist rising\nup from below obscures all view of the far side.  A sw path leads away\nfrom the chasm into a winding corridor.", sound = SILENT, loud = false), // 117: LOC_SWCHASM
    Location(small = "You're in sloping corridor.", big = "You are in a long winding corridor sloping out of sight in both\ndirections.", sound = SILENT, loud = false), // 118: LOC_WINDING
    Location(small = null, big = "You are in a secret canyon which exits to the north and east.", sound = SILENT, loud = false), // 119: LOC_SECRET4
    Location(small = null, big = "You are in a secret canyon which exits to the north and east.", sound = SILENT, loud = false), // 120: LOC_SECRET5
    Location(small = null, big = "You are in a secret canyon which exits to the north and east.", sound = SILENT, loud = false), // 121: LOC_SECRET6
    Location(small = "You're on ne side of chasm.", big = "You are on the far side of the chasm.  A ne path leads away from the\nchasm on this side.", sound = SILENT, loud = false), // 122: LOC_NECHASM
    Location(small = "You're in corridor.", big = "You're in a long east/west corridor.  A faint rumbling noise can be\nheard in the distance.", sound = DULL_RUMBLING, loud = false), // 123: LOC_CORRIDOR
    Location(small = "You're at fork in path.", big = "The path forks here.  The left fork leads northeast.  A dull rumbling\nseems to get louder in that direction.  The right fork leads southeast\ndown a gentle slope.  The main corridor enters from the west.", sound = DULL_RUMBLING, loud = false), // 124: LOC_FORK
    Location(small = "You're at junction with warm walls.", big = "The walls are quite warm here.  From the north can be heard a steady\nroar, so loud that the entire cave seems to be trembling.  Another\npassage leads south, and a low crawl goes east.", sound = LOUD_ROAR, loud = false), // 125: LOC_WARMWALLS
    Location(small = "You're at breath-taking view.", big = "You are on the edge of a breath-taking view.  Far below you is an\nactive volcano, from which great gouts of molten lava come surging\nout, cascading back down into the depths.  The glowing rock fills the\nfarthest reaches of the cavern with a blood-red glare, giving every-\nthing an eerie, macabre appearance.  The air is filled with flickering\nsparks of ash and a heavy smell of brimstone.  The walls are hot to\nthe touch, and the thundering of the volcano drowns out all other\nsounds.  Embedded in the jagged roof far overhead are myriad twisted\nformations composed of pure white alabaster, which scatter the murky\nlight into sinister apparitions upon the walls.  To one side is a deep\ngorge, filled with a bizarre chaos of tortured rock which seems to\nhave been crafted by the devil himself.  An immense river of fire\ncrashes out from the depths of the volcano, burns its way through the\ngorge, and plummets into a bottomless pit far off to your left.  To\nthe right, an immense geyser of blistering steam erupts continuously\nfrom a barren island in the center of a sulfurous lake, which bubbles\nominously.  The far right wall is aflame with an incandescence of its\nown, which lends an additional infernal splendor to the already\nhellish scene.  A dark, foreboding passage exits to the south.", sound = TOTAL_ROAR, loud = true), // 126: LOC_BREATHTAKING
    Location(small = "You're in Chamber of Boulders.", big = "You are in a small chamber filled with large boulders.  The walls are\nvery warm, causing the air in the room to be almost stifling from the\nheat.  The only exit is a crawl heading west, through which is coming\na low rumbling.", sound = DULL_RUMBLING, loud = false), // 127: LOC_BOULDERS2
    Location(small = "You're in limestone passage.", big = "You are walking along a gently sloping north/south passage lined with\noddly shaped limestone formations.", sound = SILENT, loud = false), // 128: LOC_LIMESTONE
    Location(small = "You're in front of Barren Room.", big = "You are standing at the entrance to a large, barren room.  A notice\nabove the entrance reads:  \"Caution!  Bear in room!\"", sound = SILENT, loud = false), // 129: LOC_BARRENFRONT
    Location(small = "You're in Barren Room.", big = "You are inside a barren room.  The center of the room is completely\nempty except for some dust.  Marks in the dust lead away toward the\nfar end of the room.  The only exit is the way you came in.", sound = SILENT, loud = false), // 130: LOC_BARRENROOM
    Location(small = null, big = "You are in a maze of twisting little passages, all different.", sound = SILENT, loud = false), // 131: LOC_DIFFERENT3
    Location(small = null, big = "You are in a little maze of twisty passages, all different.", sound = SILENT, loud = false), // 132: LOC_DIFFERENT4
    Location(small = null, big = "You are in a twisting maze of little passages, all different.", sound = SILENT, loud = false), // 133: LOC_DIFFERENT5
    Location(small = null, big = "You are in a twisting little maze of passages, all different.", sound = SILENT, loud = false), // 134: LOC_DIFFERENT6
    Location(small = null, big = "You are in a twisty little maze of passages, all different.", sound = SILENT, loud = false), // 135: LOC_DIFFERENT7
    Location(small = null, big = "You are in a twisty maze of little passages, all different.", sound = SILENT, loud = false), // 136: LOC_DIFFERENT8
    Location(small = null, big = "You are in a little twisty maze of passages, all different.", sound = SILENT, loud = false), // 137: LOC_DIFFERENT9
    Location(small = null, big = "You are in a maze of little twisting passages, all different.", sound = SILENT, loud = false), // 138: LOC_DIFFERENT10
    Location(small = null, big = "You are in a maze of little twisty passages, all different.", sound = SILENT, loud = false), // 139: LOC_DIFFERENT11
    Location(small = null, big = "Dead end", sound = SILENT, loud = false), // 140: LOC_DEADEND13
    Location(small = null, big = "You are in a long, rough-hewn, north/south corridor.", sound = SILENT, loud = false), // 141: LOC_ROUGHHEWN
    Location(small = null, big = "There is no way to go that direction.", sound = SILENT, loud = false), // 142: LOC_BADDIRECTION
    Location(small = null, big = "You are in a large chamber with passages to the west and north.", sound = SILENT, loud = false), // 143: LOC_LARGE
    Location(small = null, big = "You are in the ogre's storeroom.  The only exit is to the south.", sound = SILENT, loud = false), // 144: LOC_STOREROOM
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 145: LOC_FOREST1
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 146: LOC_FOREST2
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 147: LOC_FOREST3
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 148: LOC_FOREST4
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 149: LOC_FOREST5
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 150: LOC_FOREST6
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 151: LOC_FOREST7
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 152: LOC_FOREST8
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 153: LOC_FOREST9
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 154: LOC_FOREST10
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 155: LOC_FOREST11
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 156: LOC_FOREST12
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 157: LOC_FOREST13
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 158: LOC_FOREST14
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 159: LOC_FOREST15
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 160: LOC_FOREST16
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 161: LOC_FOREST17
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 162: LOC_FOREST18
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 163: LOC_FOREST19
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 164: LOC_FOREST20
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 165: LOC_FOREST21
    Location(small = null, big = "You are wandering aimlessly through the forest.", sound = SILENT, loud = false), // 166: LOC_FOREST22
    Location(small = "You're on ledge.", big = "You are on a small ledge on one face of a sheer cliff.  There are no\npaths away from the ledge.  Across the chasm is a small clearing\nsurrounded by forest.", sound = SILENT, loud = false), // 167: LOC_LEDGE
    Location(small = "You're at bottom of reservoir.", big = "You are walking across the bottom of the reservoir.  Walls of water\nrear up on either side.  The roar of the water cascading past is\nnearly deafening, and the mist is so thick you can barely see.", sound = TOTAL_ROAR, loud = true), // 168: LOC_RESBOTTOM
    Location(small = "You're north of reservoir.", big = "You are at the northern edge of the reservoir.  A northwest passage\nleads sharply up from here.", sound = WATERS_CRASHING, loud = false), // 169: LOC_RESNORTH
    Location(small = null, big = "You are scrambling along a treacherously steep, rocky passage.", sound = SILENT, loud = false), // 170: LOC_TREACHEROUS
    Location(small = null, big = "You are on a very steep incline, which widens at it goes upward.", sound = SILENT, loud = false), // 171: LOC_STEEP
    Location(small = "You're at base of cliff.", big = "You are at the base of a nearly vertical cliff.  There are some\nslim footholds which would enable you to climb up, but it looks\nextremely dangerous.  Here at the base of the cliff lie the remains\nof several earlier adventurers who apparently failed to make it.", sound = SILENT, loud = false), // 172: LOC_CLIFFBASE
    Location(small = null, big = "You are climbing along a nearly vertical cliff.", sound = SILENT, loud = false), // 173: LOC_CLIFFACE
    Location(small = null, big = "Just as you reach the top, your foot slips on a loose rock and you\ntumble several hundred feet to join the other unlucky adventurers.", sound = SILENT, loud = false), // 174: LOC_FOOTSLIP
    Location(small = null, big = "Just as you reach the top, your foot slips on a loose rock and you\nmake one last desperate grab.  Your luck holds, as does your grip.\nWith an enormous heave, you lift yourself to the ledge above.", sound = SILENT, loud = false), // 175: LOC_CLIFFTOP
    Location(small = "You're at top of cliff.", big = "You are on a small ledge at the top of a nearly vertical cliff.\nThere is a low crawl leading off to the northeast.", sound = SILENT, loud = false), // 176: LOC_CLIFFLEDGE
    Location(small = null, big = "You have reached a dead end.", sound = SILENT, loud = false), // 177: LOC_REACHDEAD
    Location(small = null, big = "There is now one more gruesome aspect to the spectacular vista.", sound = SILENT, loud = false), // 178: LOC_GRUESOME
    Location(small = null, big = ">>Foof!<<", sound = SILENT, loud = false), // 179: LOC_FOOF1
    Location(small = null, big = ">>Foof!<<", sound = SILENT, loud = false), // 180: LOC_FOOF2
    Location(small = null, big = ">>Foof!<<", sound = SILENT, loud = false), // 181: LOC_FOOF3
    Location(small = null, big = ">>Foof!<<", sound = SILENT, loud = false), // 182: LOC_FOOF4
    Location(small = null, big = ">>Foof!<<", sound = SILENT, loud = false), // 183: LOC_FOOF5
    Location(small = null, big = ">>Foof!<<", sound = SILENT, loud = false), // 184: LOC_FOOF6
)

val objects: Array<Obj> = arrayOf(
    Obj( // 0: NO_OBJECT
        words = emptyList(),
        inventory = null,
        plac = LOC_NOWHERE, fixd = LOC_NOWHERE,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 1: KEYS
        words = listOf("keys", "key"),
        inventory = "Set of keys",
        plac = LOC_BUILDING, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There are some keys on the ground here."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 2: LAMP
        words = listOf("lamp", "lante"),
        inventory = "Brass lantern",
        plac = LOC_BUILDING, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There is a shiny brass lamp nearby.", "There is a lamp shining nearby."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("Your lamp is now off.", "Your lamp is now on."),
    ),
    Obj( // 3: GRATE
        words = listOf("grate"),
        inventory = "*grate",
        plac = LOC_GRATE, fixd = LOC_BELOWGRATE,
        isTreasure = false,
        descriptions = listOf("The grate is locked.", "The grate is open."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("The grate is now locked.", "The grate is now unlocked."),
    ),
    Obj( // 4: CAGE
        words = listOf("cage"),
        inventory = "Wicker cage",
        plac = LOC_COBBLE, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There is a small wicker cage discarded nearby."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 5: ROD
        words = listOf("rod"),
        inventory = "Black rod",
        plac = LOC_DEBRIS, fixd = 0,
        isTreasure = false,
        descriptions = listOf("A three foot black rod with a rusty star on an end lies nearby."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 6: ROD2
        words = listOf("rod"),
        inventory = "Black rod",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = false,
        descriptions = listOf("A three foot black rod with a rusty mark on an end lies nearby."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 7: STEPS
        words = listOf("steps"),
        inventory = "*steps",
        plac = LOC_PITTOP, fixd = LOC_MISTHALL,
        isTreasure = false,
        descriptions = listOf("Rough stone steps lead down the pit.", "Rough stone steps lead up the dome."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 8: BIRD
        words = listOf("bird"),
        inventory = "Little bird in cage",
        plac = LOC_BIRDCHAMBER, fixd = 0,
        isTreasure = false,
        descriptions = listOf("A cheerful little bird is sitting here singing.", "There is a little bird in the cage.", "A cheerful little bird is sitting here singing."),
        sounds = listOf("The bird's singing is quite melodious.", "The bird does not seem inclined to sing while in the cage.", "It almost seems as though the bird is trying to tell you something.", "To your surprise, you can understand the bird's chirping; it is\nsinging about the joys of its forest home.", "The bird does not seem inclined to sing while in the cage.", "The bird is singing to you in gratitude for your having returned it to\nits home.  In return, it informs you of a magic word which it thinks\nyou may find useful somewhere near the Hall of Mists.  The magic word\nchanges frequently, but for now the bird believes it is \"%s\".  You\nthank the bird for this information, and it flies off into the forest."),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 9: DOOR
        words = listOf("door"),
        inventory = "*rusty door",
        plac = LOC_IMMENSE, fixd = -1,
        isTreasure = false,
        descriptions = listOf("The way north is barred by a massive, rusty, iron door.", "The way north leads through a massive, rusty, iron door."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("The hinges are quite thoroughly rusted now and won't budge.", "The oil has freed up the hinges so that the door will now move,\nalthough it requires some effort."),
    ),
    Obj( // 10: PILLOW
        words = listOf("pillo", "velve"),
        inventory = "Velvet pillow",
        plac = LOC_SOFTROOM, fixd = 0,
        isTreasure = false,
        descriptions = listOf("A small velvet pillow lies on the floor."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 11: SNAKE
        words = listOf("snake"),
        inventory = "*snake",
        plac = LOC_KINGHALL, fixd = -1,
        isTreasure = false,
        descriptions = listOf("A huge green fierce snake bars the way!", ""),
        sounds = listOf("The snake is hissing venomously.", ""),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 12: FISSURE
        words = listOf("fissu"),
        inventory = "*fissure",
        plac = LOC_EASTBANK, fixd = LOC_WESTBANK,
        isTreasure = false,
        descriptions = listOf("", "A crystal bridge spans the fissure."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("The crystal bridge has vanished!", "A crystal bridge now spans the fissure."),
    ),
    Obj( // 13: OBJ_13
        words = listOf("table"),
        inventory = "*stone tablet",
        plac = LOC_DARKROOM, fixd = -1,
        isTreasure = false,
        descriptions = listOf("A massive stone tablet embedded in the wall reads:\n\"Congratulations on bringing light into the dark-room!\""),
        sounds = emptyList(),
        texts = listOf("\"Congratulations on bringing light into the dark-room!\""),
        changes = emptyList(),
    ),
    Obj( // 14: CLAM
        words = listOf("clam"),
        inventory = "Giant clam  >GRUNT!<",
        plac = LOC_SHELLROOM, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There is an enormous clam here with its shell tightly closed."),
        sounds = listOf("The clam is as tight-mouthed as a, er, clam."),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 15: OYSTER
        words = listOf("oyste"),
        inventory = "Giant oyster  >GROAN!<",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There is an enormous oyster here with its shell tightly closed.", "Interesting.  There seems to be something written on the underside of\nthe oyster."),
        sounds = listOf("Even though it's an oyster, the critter's as tight-mouthed as a clam.", "It says the same thing it did before.  Hm, maybe it's a pun?"),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 16: MAGAZINE
        words = listOf("magaz", "issue", "spelu", "\"spel"),
        inventory = "\"Spelunker Today\"",
        plac = LOC_ANTEROOM, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There are a few recent issues of \"Spelunker Today\" magazine here."),
        sounds = emptyList(),
        texts = listOf("I'm afraid the magazine is written in dwarvish.  But penciled on one\ncover you see, \"Please leave the magazines at the construction site.\""),
        changes = emptyList(),
    ),
    Obj( // 17: DWARF
        words = listOf("dwarf", "dwarv"),
        inventory = null,
        plac = LOC_NOWHERE, fixd = -1,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 18: KNIFE
        words = listOf("knife", "knive"),
        inventory = null,
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 19: FOOD
        words = listOf("food", "ratio"),
        inventory = "Tasty food",
        plac = LOC_BUILDING, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There is food here."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 20: BOTTLE
        words = listOf("bottl", "jar"),
        inventory = "Small bottle",
        plac = LOC_BUILDING, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There is a bottle of water here.", "There is an empty bottle here.", "There is a bottle of oil here."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("Your bottle is now full of water.", "The bottle of water is now empty.", "Your bottle is now full of oil."),
    ),
    Obj( // 21: WATER
        words = listOf("water", "h2o"),
        inventory = "Water in the bottle",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 22: OIL
        words = listOf("oil"),
        inventory = "Oil in the bottle",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 23: MIRROR
        words = listOf("mirro"),
        inventory = "*mirror",
        plac = LOC_MIRRORCANYON, fixd = -1,
        isTreasure = false,
        descriptions = listOf("", ""),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("", "You strike the mirror a resounding blow, whereupon it shatters into a\nmyriad tiny fragments."),
    ),
    Obj( // 24: PLANT
        words = listOf("plant", "beans"),
        inventory = "*plant",
        plac = LOC_WESTPIT, fixd = -1,
        isTreasure = false,
        descriptions = listOf("There is a tiny little plant in the pit, murmuring \"water, water, ...\"", "There is a 12-foot-tall beanstalk stretching up out of the pit,\nbellowing \"WATER!! WATER!!\"", "There is a gigantic beanstalk stretching all the way up to the hole."),
        sounds = listOf("The plant continues to ask plaintively for water.", "The plant continues to demand water.", "The plant now maintains a contented silence."),
        texts = emptyList(),
        changes = listOf("You've over-watered the plant!  It's shriveling up!  And now . . .", "The plant spurts into furious growth for a few seconds.", "The plant grows explosively, almost filling the bottom of the pit."),
    ),
    Obj( // 25: PLANT2
        words = listOf("plant"),
        inventory = "*phony plant",
        plac = LOC_WESTEND, fixd = LOC_EASTEND,
        isTreasure = false,
        descriptions = listOf("", "The top of a 12-foot-tall beanstalk is poking out of the west pit.", "There is a huge beanstalk growing out of the west pit up to the hole."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 26: OBJ_26
        words = listOf("stala"),
        inventory = "*stalactite",
        plac = LOC_TOPSTALACTITE, fixd = -1,
        isTreasure = false,
        descriptions = listOf(""),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 27: OBJ_27
        words = listOf("shado", "figur", "windo"),
        inventory = "*shadowy figure and/or window",
        plac = LOC_WINDOW1, fixd = LOC_WINDOW2,
        isTreasure = false,
        descriptions = listOf("The shadowy figure seems to be trying to attract your attention."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 28: AXE
        words = listOf("axe"),
        inventory = "Dwarf's axe",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There is a little axe here.", "There is a little axe lying beside the bear."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("", "The axe misses and lands near the bear where you can't get at it."),
    ),
    Obj( // 29: OBJ_29
        words = listOf("drawi"),
        inventory = "*cave drawings",
        plac = LOC_ORIENTAL, fixd = -1,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 30: OBJ_30
        words = listOf("pirat", "genie", "djinn"),
        inventory = "*pirate/genie",
        plac = LOC_NOWHERE, fixd = -1,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 31: DRAGON
        words = listOf("drago"),
        inventory = "*dragon",
        plac = LOC_SECRET4, fixd = LOC_SECRET6,
        isTreasure = false,
        descriptions = listOf("A huge green fierce dragon bars the way!", "The blood-specked body of a huge green dead dragon lies to one side.", "The body of a huge green dead dragon is lying off to one side."),
        sounds = listOf("The dragon's ominous hissing does not bode well for you.", "The dragon is, not surprisingly, silent.", "The dragon is, not surprisingly, silent."),
        texts = emptyList(),
        changes = listOf("", "Congratulations!  You have just vanquished a dragon with your bare\nhands!  (Unbelievable, isn't it?)", "Your head buzzes strangely for a moment."),
    ),
    Obj( // 32: CHASM
        words = listOf("chasm"),
        inventory = "*chasm",
        plac = LOC_SWCHASM, fixd = LOC_NECHASM,
        isTreasure = false,
        descriptions = listOf("A rickety wooden bridge extends across the chasm, vanishing into the\nmist.  A notice posted on the bridge reads, \"Stop! Pay troll!\"", "The wreckage of a bridge (and a dead bear) can be seen at the bottom\nof the chasm."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("", "Just as you reach the other side, the bridge buckles beneath the\nweight of the bear, which was still following you around.  You\nscrabble desperately for support, but as the bridge collapses you\nstumble back and fall into the chasm."),
    ),
    Obj( // 33: TROLL
        words = listOf("troll"),
        inventory = "*troll",
        plac = LOC_SWCHASM, fixd = LOC_NECHASM,
        isTreasure = false,
        descriptions = listOf("A burly troll stands by the bridge and insists you throw him a\ntreasure before you may cross.", "The troll steps out from beneath the bridge and blocks your way.", ""),
        sounds = listOf("The troll sounds quite adamant in his demand for a treasure.", "The troll sounds quite adamant in his demand for a treasure.", ""),
        texts = emptyList(),
        changes = listOf("", "", "The bear lumbers toward the troll, who lets out a startled shriek and\nscurries away.  The bear soon gives up the pursuit and wanders back."),
    ),
    Obj( // 34: TROLL2
        words = listOf("troll"),
        inventory = "*phony troll",
        plac = LOC_NOWHERE, fixd = LOC_NOWHERE,
        isTreasure = false,
        descriptions = listOf("The troll is nowhere to be seen."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 35: BEAR
        words = listOf("bear"),
        inventory = null,
        plac = LOC_BARRENROOM, fixd = -1,
        isTreasure = false,
        descriptions = listOf("There is a ferocious cave bear eyeing you from the far end of the room!", "There is a gentle cave bear sitting placidly in one corner.", "There is a contented-looking bear wandering about nearby.", ""),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("", "The bear eagerly wolfs down your food, after which he seems to calm\ndown considerably and even becomes rather friendly.", "", ""),
    ),
    Obj( // 36: MESSAG
        words = listOf("messa"),
        inventory = "*message in second maze",
        plac = LOC_NOWHERE, fixd = -1,
        isTreasure = false,
        descriptions = listOf("There is a message scrawled in the dust in a flowery script, reading:\n\"This is not the maze where the pirate leaves his treasure chest.\""),
        sounds = emptyList(),
        texts = listOf("\"This is not the maze where the pirate leaves his treasure chest.\""),
        changes = emptyList(),
    ),
    Obj( // 37: VOLCANO
        words = listOf("volca", "geyse"),
        inventory = "*volcano and/or geyser",
        plac = LOC_BREATHTAKING, fixd = -1,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 38: VEND
        words = listOf("machi", "vendi"),
        inventory = "*vending machine",
        plac = LOC_DEADEND13, fixd = -1,
        isTreasure = false,
        descriptions = listOf("There is a massive and somewhat battered vending machine here.  The\ninstructions on it read: \"Drop coins here to receive fresh batteries.\"", "There is a massive vending machine here, swung back to reveal a\nsouthward passage."),
        sounds = emptyList(),
        texts = listOf("\"Drop coins here to receive fresh batteries.\"", "\"Drop coins here to receive fresh batteries.\""),
        changes = listOf("The vending machine swings back to block the passage.", "As you strike the vending machine, it pivots backward along with a\nsection of wall, revealing a dark passage leading south."),
    ),
    Obj( // 39: BATTERY
        words = listOf("batte"),
        inventory = "Batteries",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = false,
        descriptions = listOf("There are fresh batteries here.", "Some worn-out batteries have been discarded nearby."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 40: OBJ_40
        words = listOf("carpe", "moss"),
        inventory = "*carpet and/or moss and/or curtains",
        plac = LOC_SOFTROOM, fixd = -1,
        isTreasure = false,
        descriptions = emptyList(),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 41: OGRE
        words = listOf("ogre"),
        inventory = "*ogre",
        plac = LOC_LARGE, fixd = -1,
        isTreasure = false,
        descriptions = listOf("A formidable ogre bars the northern exit."),
        sounds = listOf("The ogre is apparently the strong, silent type."),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 42: URN
        words = listOf("urn"),
        inventory = "*urn",
        plac = LOC_CLIFF, fixd = -1,
        isTreasure = false,
        descriptions = listOf("A small urn is embedded in the rock.", "A small urn full of oil is embedded in the rock.", "A small oil flame extrudes from an urn embedded in the rock."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("The urn is empty and will not light.", "The urn is now dark.", "The urn is now lit."),
    ),
    Obj( // 43: CAVITY
        words = listOf("cavit"),
        inventory = "*cavity",
        plac = LOC_NOWHERE, fixd = -1,
        isTreasure = false,
        descriptions = listOf("", "There is a small urn-shaped cavity in the rock."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 44: BLOOD
        words = listOf("blood"),
        inventory = "*blood",
        plac = LOC_NOWHERE, fixd = -1,
        isTreasure = false,
        descriptions = listOf(""),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 45: RESER
        words = listOf("reser"),
        inventory = "*reservoir",
        plac = LOC_RESERVOIR, fixd = LOC_RESNORTH,
        isTreasure = false,
        descriptions = listOf("", "The waters have parted to form a narrow path across the reservoir."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("The waters crash together again.", "The waters have parted to form a narrow path across the reservoir."),
    ),
    Obj( // 46: RABBITFOOT
        words = listOf("appen", "lepor"),
        inventory = "Leporine appendage",
        plac = LOC_FOREST22, fixd = 0,
        isTreasure = false,
        descriptions = listOf("Your keen eye spots a severed leporine appendage lying on the ground."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 47: OBJ_47
        words = listOf("mud"),
        inventory = "*mud",
        plac = LOC_DEBRIS, fixd = -1,
        isTreasure = false,
        descriptions = listOf(""),
        sounds = emptyList(),
        texts = listOf("\"MAGIC WORD XYZZY\""),
        changes = emptyList(),
    ),
    Obj( // 48: OBJ_48
        words = listOf("note"),
        inventory = "*note",
        plac = LOC_NUGGET, fixd = -1,
        isTreasure = false,
        descriptions = listOf(""),
        sounds = emptyList(),
        texts = listOf("\"You won't get it up the steps\""),
        changes = emptyList(),
    ),
    Obj( // 49: SIGN
        words = listOf("sign"),
        inventory = "*sign",
        plac = LOC_ANTEROOM, fixd = -1,
        isTreasure = false,
        descriptions = listOf("", ""),
        sounds = emptyList(),
        texts = listOf("Cave under construction beyond this point.\n           Proceed at own risk.\n       [Witt Construction Company]", "\"Treasure Vault.  Keys in main office.\""),
        changes = emptyList(),
    ),
    Obj( // 50: NUGGET
        words = listOf("gold", "nugge"),
        inventory = "Large gold nugget",
        plac = LOC_NUGGET, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is a large sparkling nugget of gold here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 51: OBJ_51
        words = listOf("diamo"),
        inventory = "Several diamonds",
        plac = LOC_WESTBANK, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There are diamonds here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 52: OBJ_52
        words = listOf("silve", "bars"),
        inventory = "Bars of silver",
        plac = LOC_FLOORHOLE, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There are bars of silver here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 53: OBJ_53
        words = listOf("jewel"),
        inventory = "Precious jewelry",
        plac = LOC_SOUTHSIDE, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is precious jewelry here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 54: COINS
        words = listOf("coins"),
        inventory = "Rare coins",
        plac = LOC_WESTSIDE, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There are many coins here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 55: CHEST
        words = listOf("chest", "box", "treas"),
        inventory = "Treasure chest",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = true,
        descriptions = listOf("The pirate's treasure chest is here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 56: EGGS
        words = listOf("eggs", "egg", "nest"),
        inventory = "Golden eggs",
        plac = LOC_GIANTROOM, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is a large nest here, full of golden eggs!", "The nest of golden eggs has vanished!", "Done!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 57: TRIDENT
        words = listOf("tride"),
        inventory = "Jeweled trident",
        plac = LOC_WATERFALL, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is a jewel-encrusted trident here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 58: VASE
        words = listOf("vase", "ming", "shard", "potte"),
        inventory = "Ming vase",
        plac = LOC_ORIENTAL, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is a delicate, precious, ming vase here!", "The floor is littered with worthless shards of pottery.", "The floor is littered with worthless shards of pottery."),
        sounds = emptyList(),
        texts = emptyList(),
        changes = listOf("The vase is now resting, delicately, on a velvet pillow.", "The ming vase drops with a delicate crash.", "You have taken the vase and hurled it delicately to the ground."),
    ),
    Obj( // 59: EMERALD
        words = listOf("emera"),
        inventory = "Egg-sized emerald",
        plac = LOC_PLOVER, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is an emerald here the size of a plover's egg!", "There is an emerald resting in a small cavity in the rock!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 60: PYRAMID
        words = listOf("plati", "pyram"),
        inventory = "Platinum pyramid",
        plac = LOC_DARKROOM, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is a platinum pyramid here, 8 inches on a side!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 61: PEARL
        words = listOf("pearl"),
        inventory = "Glistening pearl",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = true,
        descriptions = listOf("Off to one side lies a glistening pearl!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 62: RUG
        words = listOf("rug", "persi"),
        inventory = "Persian rug",
        plac = LOC_SECRET4, fixd = LOC_SECRET6,
        isTreasure = true,
        descriptions = listOf("There is a Persian rug spread out on the floor!", "The dragon is sprawled out on a Persian rug!!", "There is a Persian rug here, hovering in mid-air!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 63: OBJ_63
        words = listOf("spice"),
        inventory = "Rare spices",
        plac = LOC_BOULDERS2, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There are rare spices here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 64: CHAIN
        words = listOf("chain"),
        inventory = "Golden chain",
        plac = LOC_BARRENROOM, fixd = -1,
        isTreasure = true,
        descriptions = listOf("There is a golden chain lying in a heap on the floor!", "The bear is locked to the wall with a golden chain!", "There is a golden chain locked to the wall!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 65: RUBY
        words = listOf("ruby"),
        inventory = "Giant ruby",
        plac = LOC_STOREROOM, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is an enormous ruby here!", "There is a ruby resting in a small cavity in the rock!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 66: JADE
        words = listOf("jade", "neckl"),
        inventory = "Jade necklace",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = true,
        descriptions = listOf("A precious jade necklace has been dropped here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 67: AMBER
        words = listOf("amber", "gemst"),
        inventory = "Amber gemstone",
        plac = LOC_NOWHERE, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is a rare amber gemstone here!", "There is an amber gemstone resting in a small cavity in the rock!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 68: SAPPH
        words = listOf("sapph"),
        inventory = "Star sapphire",
        plac = LOC_LEDGE, fixd = 0,
        isTreasure = true,
        descriptions = listOf("A brilliant blue star sapphire is here!", "There is a star sapphire resting in a small cavity in the rock!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
    Obj( // 69: OBJ_69
        words = listOf("ebony", "statu"),
        inventory = "Ebony statuette",
        plac = LOC_REACHDEAD, fixd = 0,
        isTreasure = true,
        descriptions = listOf("There is a richly-carved ebony statuette here!"),
        sounds = emptyList(),
        texts = emptyList(),
        changes = emptyList(),
    ),
)

val obituaries: Array<Obituary> = arrayOf(
    Obituary(query = "Oh dear, you seem to have gotten yourself killed.  I might be able to\nhelp you out, but I've never really done this before.  Do you want me\nto try to reincarnate you?", yesResponse = "All right.  But don't blame me if something goes wr......\n                    --- POOF!! ---\nYou are engulfed in a cloud of orange smoke.  Coughing and gasping,\nyou emerge from the smoke and find...."),
    Obituary(query = "You clumsy oaf, you've done it again!  I don't know how long I can\nkeep this up.  Do you want me to try reincarnating you again?", yesResponse = "Okay, now where did I put my orange smoke?....  >POOF!<\nEverything disappears in a dense cloud of orange smoke."),
    Obituary(query = "Now you've really done it!  I'm out of orange smoke!  You don't expect\nme to do a decent reincarnation without any orange smoke, do you?", yesResponse = "Okay, if you're so smart, do it yourself!  I'm leaving!"),
)

val hints: Array<Hint> = arrayOf(
    Hint(number = 1, penalty = 2, turns = 4, question = "Are you trying to get into the cave?", hint = "The grate is very solid and has a hardened steel lock.  You cannot\nenter without a key, and there are no keys nearby.  I would recommend\nlooking elsewhere for the keys."),
    Hint(number = 2, penalty = 2, turns = 5, question = "Are you trying to catch the bird?", hint = "Something about you seems to be frightening the bird.  Perhaps you\nmight figure out what it is."),
    Hint(number = 3, penalty = 2, turns = 8, question = "Are you trying to somehow deal with the snake?", hint = "You can't kill the snake, or drive it away, or avoid it, or anything\nlike that.  There is a way to get by, but you don't have the necessary\nresources right now."),
    Hint(number = 4, penalty = 4, turns = 75, question = "Do you need help getting out of the maze?", hint = "You can make the passages look less alike by dropping things."),
    Hint(number = 5, penalty = 5, turns = 25, question = "Are you trying to explore beyond the plover room?", hint = "There is a way to explore that region without having to worry about\nfalling into a pit.  None of the objects available is immediately\nuseful in discovering the secret."),
    Hint(number = 6, penalty = 3, turns = 20, question = "Do you need help getting out of here?", hint = "Don't go west.\n"),
    Hint(number = 7, penalty = 2, turns = 8, question = "Are you wondering what to do here?", hint = "This section is quite advanced.  Find the cave first.\n"),
    Hint(number = 8, penalty = 2, turns = 25, question = "Would you like to be shown out of the forest?", hint = "Go east ten times.  If that doesn't get you out, then go south, then\nwest twice, then south."),
    Hint(number = 9, penalty = 4, turns = 10, question = "Do you need help dealing with the ogre?", hint = "There is nothing the presence of which will prevent you from defeating\nhim; thus it can't hurt to fetch everything you possibly can."),
    Hint(number = 10, penalty = 4, turns = 1, question = "You're missing only one other treasure.  Do you need help finding it?", hint = "Once you've found all the other treasures, it is no longer possible to\nlocate the one you're now missing."),
)

val classes: Array<ClassMsg> = arrayOf(
    ClassMsg(threshold = 0, message = null),
    ClassMsg(threshold = 45, message = "You are obviously a rank amateur.  Better luck next time."),
    ClassMsg(threshold = 120, message = "Your score qualifies you as a novice class adventurer."),
    ClassMsg(threshold = 170, message = "You have achieved the rating: \"Experienced Adventurer\"."),
    ClassMsg(threshold = 250, message = "You may now consider yourself a \"Seasoned Adventurer\"."),
    ClassMsg(threshold = 320, message = "You have reached \"Junior Master\" status."),
    ClassMsg(threshold = 375, message = "Your score puts you in Master Adventurer Class C."),
    ClassMsg(threshold = 410, message = "Your score puts you in Master Adventurer Class B."),
    ClassMsg(threshold = 426, message = "Your score puts you in Master Adventurer Class A."),
    ClassMsg(threshold = 429, message = "All of Adventuredom gives tribute to you, Adventurer Grandmaster!"),
    ClassMsg(threshold = 9999, message = "Adventuredom stands in awe -- you have now joined the ranks of the\n       W O R L D   C H A M P I O N   A D V E N T U R E R S !\nIt may interest you to know that the Dungeon-Master himself has, to\nmy knowledge, never achieved this threshold in fewer than 330 turns."),
)

val turnThresholds: Array<TurnThreshold> = arrayOf(
    TurnThreshold(threshold = 350, pointLoss = 2, message = "Tsk!  A wizard wouldn't have to take 350 turns.  This is going to cost\nyou a couple of points."),
    TurnThreshold(threshold = 500, pointLoss = 3, message = "500 turns?  That's another few points you've lost."),
    TurnThreshold(threshold = 1000, pointLoss = 5, message = "Are you still at it?  Five points off for exceeding 1000 turns!"),
    TurnThreshold(threshold = 2500, pointLoss = 10, message = "Good grief, don't you *EVER* give up?  Do you realize you've spent\nover 2500 turns at this?  That's another ten points off, a total of\ntwenty points lost for taking so long."),
)

val conditions: IntArray = intArrayOf(
    0, // LOC_NOWHERE
    (1 shl COND_FLUID) or (1 shl COND_ABOVE) or (1 shl COND_LIT), // LOC_START
    (1 shl COND_ABOVE) or (1 shl COND_LIT), // LOC_HILL
    (1 shl COND_FLUID) or (1 shl COND_ABOVE) or (1 shl COND_LIT), // LOC_BUILDING
    (1 shl COND_FLUID) or (1 shl COND_ABOVE) or (1 shl COND_LIT), // LOC_VALLEY
    (1 shl COND_ABOVE) or (1 shl COND_LIT), // LOC_ROADEND
    (1 shl COND_ABOVE) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HCLIFF), // LOC_CLIFF
    (1 shl COND_FLUID) or (1 shl COND_ABOVE) or (1 shl COND_LIT), // LOC_SLIT
    (1 shl COND_ABOVE) or (1 shl COND_LIT) or (1 shl COND_HCAVE) or (1 shl COND_HJADE), // LOC_GRATE
    (1 shl COND_LIT), // LOC_BELOWGRATE
    (1 shl COND_LIT), // LOC_COBBLE
    0, // LOC_DEBRIS
    0, // LOC_AWKWARD
    (1 shl COND_HBIRD), // LOC_BIRDCHAMBER
    0, // LOC_PITTOP
    (1 shl COND_DEEP) or (1 shl COND_HJADE), // LOC_MISTHALL
    (1 shl COND_DEEP), // LOC_CRACK
    (1 shl COND_DEEP), // LOC_EASTBANK
    (1 shl COND_DEEP), // LOC_NUGGET
    (1 shl COND_DEEP) or (1 shl COND_HSNAKE), // LOC_KINGHALL
    (1 shl COND_DEEP), // LOC_NECKBROKE
    (1 shl COND_DEEP), // LOC_NOMAKE
    (1 shl COND_DEEP), // LOC_DOME
    (1 shl COND_DEEP), // LOC_WESTEND
    (1 shl COND_FLUID) or (1 shl COND_DEEP) or (1 shl COND_OILY), // LOC_EASTPIT
    (1 shl COND_DEEP), // LOC_WESTPIT
    (1 shl COND_DEEP), // LOC_CLIMBSTALK
    (1 shl COND_DEEP), // LOC_WESTBANK
    (1 shl COND_DEEP), // LOC_FLOORHOLE
    (1 shl COND_DEEP), // LOC_SOUTHSIDE
    (1 shl COND_DEEP), // LOC_WESTSIDE
    (1 shl COND_DEEP), // LOC_BUILDING1
    (1 shl COND_DEEP), // LOC_SNAKEBLOCK
    (1 shl COND_DEEP), // LOC_Y2
    (1 shl COND_DEEP), // LOC_JUMBLE
    (1 shl COND_DEEP), // LOC_WINDOW1
    (1 shl COND_DEEP), // LOC_BROKEN
    (1 shl COND_DEEP), // LOC_SMALLPITBRINK
    (1 shl COND_FLUID) or (1 shl COND_DEEP), // LOC_SMALLPIT
    (1 shl COND_DEEP), // LOC_DUSTY
    (1 shl COND_DEEP), // LOC_PARALLEL1
    (1 shl COND_DEEP), // LOC_MISTWEST
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE1
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE2
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE3
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE4
    (1 shl COND_DEEP) or (1 shl COND_NOARRR) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_MAZEEND1
    (1 shl COND_DEEP) or (1 shl COND_NOARRR) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_MAZEEND2
    (1 shl COND_DEEP) or (1 shl COND_NOARRR) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_MAZEEND3
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE5
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE6
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE7
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE8
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE9
    (1 shl COND_DEEP) or (1 shl COND_NOARRR) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_MAZEEND4
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE10
    (1 shl COND_DEEP) or (1 shl COND_NOARRR) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_MAZEEND5
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE), // LOC_PITBRINK
    (1 shl COND_NOARRR) or (1 shl COND_DEEP) or (1 shl COND_ALLALIKE), // LOC_MAZEEND6
    (1 shl COND_DEEP), // LOC_PARALLEL2
    (1 shl COND_DEEP), // LOC_LONGEAST
    (1 shl COND_DEEP), // LOC_LONGWEST
    (1 shl COND_DEEP), // LOC_CROSSOVER
    (1 shl COND_DEEP), // LOC_DEADEND7
    (1 shl COND_DEEP) or (1 shl COND_HJADE), // LOC_COMPLEX
    (1 shl COND_DEEP), // LOC_BEDQUILT
    (1 shl COND_DEEP), // LOC_SWISSCHEESE
    (1 shl COND_DEEP), // LOC_EASTEND
    (1 shl COND_DEEP), // LOC_SLAB
    (1 shl COND_DEEP), // LOC_SECRET1
    (1 shl COND_DEEP), // LOC_SECRET2
    (1 shl COND_DEEP), // LOC_THREEJUNCTION
    (1 shl COND_DEEP), // LOC_LOWROOM
    (1 shl COND_DEEP), // LOC_DEADCRAWL
    (1 shl COND_DEEP), // LOC_SECRET3
    (1 shl COND_DEEP), // LOC_WIDEPLACE
    (1 shl COND_DEEP), // LOC_TIGHTPLACE
    (1 shl COND_DEEP), // LOC_TALL
    (1 shl COND_DEEP), // LOC_BOULDERS1
    (1 shl COND_DEEP), // LOC_SEWER
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE11
    (1 shl COND_DEEP) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_MAZEEND8
    (1 shl COND_DEEP) or (1 shl COND_NOARRR) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_MAZEEND9
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE), // LOC_ALIKE12
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE), // LOC_ALIKE13
    (1 shl COND_NOARRR) or (1 shl COND_DEEP) or (1 shl COND_ALLALIKE), // LOC_MAZEEND10
    (1 shl COND_DEEP) or (1 shl COND_NOARRR) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_MAZEEND11
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLALIKE) or (1 shl COND_HMAZE), // LOC_ALIKE14
    (1 shl COND_DEEP), // LOC_NARROW
    (1 shl COND_DEEP), // LOC_NOCLIMB
    (1 shl COND_DEEP), // LOC_PLANTTOP
    (1 shl COND_DEEP), // LOC_INCLINE
    (1 shl COND_DEEP), // LOC_GIANTROOM
    (1 shl COND_DEEP), // LOC_CAVEIN
    (1 shl COND_DEEP), // LOC_IMMENSE
    (1 shl COND_FLUID) or (1 shl COND_DEEP), // LOC_WATERFALL
    (1 shl COND_DEEP), // LOC_SOFTROOM
    (1 shl COND_DEEP), // LOC_ORIENTAL
    (1 shl COND_DEEP), // LOC_MISTY
    (1 shl COND_DEEP) or (1 shl COND_HDARK), // LOC_ALCOVE
    (1 shl COND_DEEP) or (1 shl COND_LIT) or (1 shl COND_HDARK), // LOC_PLOVER
    (1 shl COND_DEEP) or (1 shl COND_HDARK), // LOC_DARKROOM
    (1 shl COND_DEEP), // LOC_ARCHED
    (1 shl COND_DEEP), // LOC_SHELLROOM
    (1 shl COND_DEEP), // LOC_SLOPING1
    (1 shl COND_DEEP), // LOC_CULDESAC
    (1 shl COND_DEEP), // LOC_ANTEROOM
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT1
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_HWITT), // LOC_WITTSEND
    (1 shl COND_DEEP) or (1 shl COND_HJADE), // LOC_MIRRORCANYON
    (1 shl COND_DEEP), // LOC_WINDOW2
    (1 shl COND_DEEP) or (1 shl COND_ALLALIKE), // LOC_TOPSTALACTITE
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT2
    (1 shl COND_FLUID) or (1 shl COND_DEEP), // LOC_RESERVOIR
    (1 shl COND_DEEP) or (1 shl COND_ALLALIKE), // LOC_MAZEEND12
    (1 shl COND_DEEP) or (1 shl COND_LIT), // LOC_NE
    (1 shl COND_DEEP) or (1 shl COND_LIT), // LOC_SW
    (1 shl COND_DEEP), // LOC_SWCHASM
    (1 shl COND_DEEP), // LOC_WINDING
    (1 shl COND_DEEP), // LOC_SECRET4
    (1 shl COND_DEEP), // LOC_SECRET5
    (1 shl COND_DEEP), // LOC_SECRET6
    (1 shl COND_NOARRR) or (1 shl COND_DEEP), // LOC_NECHASM
    (1 shl COND_NOARRR) or (1 shl COND_DEEP), // LOC_CORRIDOR
    (1 shl COND_NOARRR) or (1 shl COND_DEEP), // LOC_FORK
    (1 shl COND_NOARRR) or (1 shl COND_DEEP), // LOC_WARMWALLS
    (1 shl COND_NOARRR) or (1 shl COND_LIT) or (1 shl COND_DEEP) or (1 shl COND_HJADE), // LOC_BREATHTAKING
    (1 shl COND_NOARRR) or (1 shl COND_DEEP), // LOC_BOULDERS2
    (1 shl COND_NOARRR) or (1 shl COND_DEEP), // LOC_LIMESTONE
    (1 shl COND_NOARRR) or (1 shl COND_DEEP), // LOC_BARRENFRONT
    (1 shl COND_NOARRR) or (1 shl COND_DEEP), // LOC_BARRENROOM
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT3
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT4
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT5
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT6
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT7
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT8
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT9
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT10
    (1 shl COND_DEEP) or (1 shl COND_NOBACK) or (1 shl COND_ALLDIFFERENT), // LOC_DIFFERENT11
    (1 shl COND_DEEP) or (1 shl COND_ALLDIFFERENT), // LOC_DEADEND13
    (1 shl COND_DEEP), // LOC_ROUGHHEWN
    (1 shl COND_DEEP), // LOC_BADDIRECTION
    (1 shl COND_DEEP) or (1 shl COND_HOGRE), // LOC_LARGE
    (1 shl COND_DEEP), // LOC_STOREROOM
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST1
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST2
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST3
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST4
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST5
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST6
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST7
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST8
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST9
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST10
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST11
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST12
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST13
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST14
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST15
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST16
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST17
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST18
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST19
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST20
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST21
    (1 shl COND_FOREST) or (1 shl COND_NOBACK) or (1 shl COND_LIT) or (1 shl COND_HWOODS), // LOC_FOREST22
    (1 shl COND_ABOVE) or (1 shl COND_LIT), // LOC_LEDGE
    (1 shl COND_FLUID) or (1 shl COND_DEEP), // LOC_RESBOTTOM
    (1 shl COND_FLUID) or (1 shl COND_DEEP), // LOC_RESNORTH
    (1 shl COND_DEEP), // LOC_TREACHEROUS
    (1 shl COND_DEEP), // LOC_STEEP
    (1 shl COND_DEEP), // LOC_CLIFFBASE
    (1 shl COND_DEEP), // LOC_CLIFFACE
    (1 shl COND_DEEP), // LOC_FOOTSLIP
    (1 shl COND_DEEP), // LOC_CLIFFTOP
    (1 shl COND_DEEP), // LOC_CLIFFLEDGE
    (1 shl COND_DEEP), // LOC_REACHDEAD
    (1 shl COND_DEEP), // LOC_GRUESOME
    0, // LOC_FOOF1
    (1 shl COND_ABOVE), // LOC_FOOF2
    (1 shl COND_DEEP), // LOC_FOOF3
    (1 shl COND_ABOVE), // LOC_FOOF4
    (1 shl COND_DEEP), // LOC_FOOF5
    (1 shl COND_DEEP), // LOC_FOOF6
)

val motions: Array<Motion> = arrayOf(
    Motion(words = emptyList()), // MOT_0
    Motion(words = emptyList()), // HERE
    Motion(words = listOf("road", "hill")), // MOT_2
    Motion(words = listOf("enter")), // ENTER
    Motion(words = listOf("upstr")), // MOT_4
    Motion(words = listOf("downs")), // MOT_5
    Motion(words = listOf("fores")), // MOT_6
    Motion(words = listOf("forwa", "conti", "onwar")), // FORWARD
    Motion(words = listOf("back", "retur", "retre")), // BACK
    Motion(words = listOf("valle")), // MOT_9
    Motion(words = listOf("stair")), // MOT_10
    Motion(words = listOf("out", "outsi", "exit", "leave")), // OUTSIDE
    Motion(words = listOf("build", "house")), // MOT_12
    Motion(words = listOf("gully")), // MOT_13
    Motion(words = listOf("strea")), // STREAM
    Motion(words = listOf("fork")), // MOT_15
    Motion(words = listOf("bed")), // MOT_16
    Motion(words = listOf("crawl")), // CRAWL
    Motion(words = listOf("cobbl")), // MOT_18
    Motion(words = listOf("inwar", "insid", "in")), // INSIDE
    Motion(words = listOf("surfa")), // MOT_20
    Motion(words = listOf("null", "nowhe")), // NUL
    Motion(words = listOf("dark")), // MOT_22
    Motion(words = listOf("passa", "tunne")), // MOT_23
    Motion(words = listOf("low")), // MOT_24
    Motion(words = listOf("canyo")), // MOT_25
    Motion(words = listOf("awkwa")), // MOT_26
    Motion(words = listOf("giant")), // MOT_27
    Motion(words = listOf("view")), // MOT_28
    Motion(words = listOf("upwar", "up", "u", "above", "ascen")), // UP
    Motion(words = listOf("d", "downw", "down", "desce")), // DOWN
    Motion(words = listOf("pit")), // MOT_31
    Motion(words = listOf("outdo")), // MOT_32
    Motion(words = listOf("crack")), // MOT_33
    Motion(words = listOf("steps")), // MOT_34
    Motion(words = listOf("dome")), // MOT_35
    Motion(words = listOf("left")), // LEFT
    Motion(words = listOf("right")), // RIGHT
    Motion(words = listOf("hall")), // MOT_38
    Motion(words = listOf("jump")), // MOT_39
    Motion(words = listOf("barre")), // MOT_40
    Motion(words = listOf("over")), // MOT_41
    Motion(words = listOf("acros")), // MOT_42
    Motion(words = listOf("east", "e")), // EAST
    Motion(words = listOf("west", "w")), // WEST
    Motion(words = listOf("north", "n")), // NORTH
    Motion(words = listOf("south", "s")), // SOUTH
    Motion(words = listOf("ne")), // NE
    Motion(words = listOf("se")), // SE
    Motion(words = listOf("sw")), // SW
    Motion(words = listOf("nw")), // NW
    Motion(words = listOf("debri")), // MOT_51
    Motion(words = listOf("hole")), // MOT_52
    Motion(words = listOf("wall")), // MOT_53
    Motion(words = listOf("broke")), // MOT_54
    Motion(words = listOf("y2")), // MOT_55
    Motion(words = listOf("climb")), // MOT_56
    Motion(words = listOf("l", "x", "look", "exami", "touch", "descr")), // LOOK
    Motion(words = listOf("floor")), // MOT_58
    Motion(words = listOf("room")), // MOT_59
    Motion(words = listOf("slit")), // MOT_60
    Motion(words = listOf("slab", "slabr")), // MOT_61
    Motion(words = listOf("xyzzy")), // XYZZY
    Motion(words = listOf("depre")), // DEPRESSION
    Motion(words = listOf("entra")), // ENTRANCE
    Motion(words = listOf("plugh")), // PLUGH
    Motion(words = listOf("secre")), // MOT_66
    Motion(words = listOf("cave")), // CAVE
    Motion(words = listOf("cross")), // CROSS
    Motion(words = listOf("bedqu")), // BEDQUILT
    Motion(words = listOf("plove")), // PLOVER
    Motion(words = listOf("orien")), // ORIENTAL
    Motion(words = listOf("caver")), // CAVERN
    Motion(words = listOf("shell")), // SHELLROOM
    Motion(words = listOf("reser")), // RESERVOIR
    Motion(words = listOf("main", "offic")), // OFFICE
)

val actions: Array<Action> = arrayOf(
    Action(words = emptyList(), message = null, noAction = false), // ACT_NULL
    Action(words = listOf("g", "carry", "take", "keep", "catch", "steal", "captu", "get", "tote", "snarf"), message = "You are already carrying it!", noAction = false), // CARRY
    Action(words = listOf("drop", "relea", "free", "disca", "dump"), message = "You aren't carrying it!", noAction = false), // DROP
    Action(words = listOf("say", "chant", "sing", "utter", "mumbl"), message = "NO_MESSAGE", noAction = false), // SAY
    Action(words = listOf("unloc", "open"), message = "I don't know how to lock or unlock such a thing.", noAction = false), // UNLOCK
    Action(words = listOf("z", "nothi"), message = "NO_MESSAGE", noAction = false), // NOTHING
    Action(words = listOf("lock", "close"), message = "I don't know how to lock or unlock such a thing.", noAction = false), // LOCK
    Action(words = listOf("light", "on"), message = "I'm afraid I don't understand.", noAction = false), // LIGHT
    Action(words = listOf("extin", "off"), message = "I'm afraid I don't understand.", noAction = false), // EXTINGUISH
    Action(words = listOf("wave", "shake", "swing"), message = "Nothing happens.", noAction = false), // WAVE
    Action(words = listOf("calm", "placa", "tame"), message = "I'm game.  Would you care to explain how?", noAction = false), // TAME
    Action(words = listOf("walk", "run", "trave", "go", "proce", "conti", "explo", "follo", "turn"), message = "Where?", noAction = false), // GO
    Action(words = listOf("attac", "kill", "fight", "hit", "strik", "slay"), message = "Don't be ridiculous!", noAction = false), // ATTACK
    Action(words = listOf("pour"), message = "You aren't carrying it!", noAction = false), // POUR
    Action(words = listOf("eat", "devou"), message = "Don't be ridiculous!", noAction = false), // EAT
    Action(words = listOf("drink"), message = "You have taken a drink from the stream.  The water tastes strongly of\nminerals, but is not unpleasant.  It is extremely cold.", noAction = false), // DRINK
    Action(words = listOf("rub"), message = "Rubbing the electric lamp is not particularly rewarding.  Anyway,\nnothing exciting happens.", noAction = false), // RUB
    Action(words = listOf("throw", "toss"), message = "You aren't carrying it!", noAction = false), // THROW
    Action(words = listOf("quit"), message = "Huh?", noAction = false), // QUIT
    Action(words = listOf("find", "where"), message = "I can only tell you what you see as you move about and manipulate\nthings.  I cannot tell you where remote things are.", noAction = false), // FIND
    Action(words = listOf("i", "inven"), message = "I can only tell you what you see as you move about and manipulate\nthings.  I cannot tell you where remote things are.", noAction = false), // INVENTORY
    Action(words = listOf("feed"), message = "There is nothing here to eat.", noAction = false), // FEED
    Action(words = listOf("fill"), message = "You can't fill that.", noAction = false), // FILL
    Action(words = listOf("blast", "deton", "ignit", "blowu"), message = "Blasting requires dynamite.", noAction = false), // BLAST
    Action(words = listOf("score"), message = "Huh?", noAction = false), // SCORE
    Action(words = listOf("fee"), message = "I don't know how.", noAction = false), // FEE
    Action(words = listOf("fie"), message = "I don't know how.", noAction = false), // FIE
    Action(words = listOf("foe"), message = "I don't know how.", noAction = false), // FOE
    Action(words = listOf("foo"), message = "I don't know how.", noAction = false), // FOO
    Action(words = listOf("fum"), message = "I don't know how.", noAction = false), // FUM
    Action(words = listOf("brief"), message = "On what?", noAction = false), // BRIEF
    Action(words = listOf("read", "perus"), message = "I'm afraid I don't understand.", noAction = false), // READ
    Action(words = listOf("break", "shatt", "smash"), message = "It is beyond your power to do that.", noAction = false), // BREAK
    Action(words = listOf("wake", "distu"), message = "Don't be ridiculous!", noAction = false), // WAKE
    Action(words = listOf("suspe", "pause", "save"), message = "Huh?", noAction = false), // SAVE
    Action(words = listOf("resum", "resta"), message = "Huh?", noAction = false), // RESUME
    Action(words = listOf("fly"), message = "I'm game.  Would you care to explain how?", noAction = false), // FLY
    Action(words = listOf("liste"), message = "I'm afraid I don't understand.", noAction = false), // LISTEN
    Action(words = listOf("z'zzz"), message = "Nothing happens.", noAction = false), // PART
    Action(words = listOf("seed"), message = "Seed set to %d", noAction = false), // SEED
    Action(words = listOf("waste"), message = "Game limit is now %d", noAction = false), // WASTE
    Action(words = emptyList(), message = "Huh?", noAction = false), // ACT_UNKNOWN
    Action(words = listOf("thank"), message = "You're quite welcome.", noAction = true), // THANKYOU
    Action(words = listOf("sesam", "opens", "abra", "abrac", "shaza", "hocus", "pocus"), message = "Good try, but that is an old worn-out magic word.", noAction = true), // INVALIDMAGIC
    Action(words = listOf("help", "?"), message = "I know of places, actions, and things.  Most of my vocabulary\ndescribes places and is used to move you there.  To move, try words\nlike forest, building, downstream, enter, east, west, north, south,\nup, or down.  I know about a few special objects, like a black rod\nhidden in the cave.  These objects can be manipulated using some of\nthe action words that I know.  Usually you will need to give both the\nobject and action words (in either order), but sometimes I can infer\nthe object from the verb alone.  Some objects also imply verbs; in\nparticular, \"inventory\" implies \"take inventory\", which causes me to\ngive you a list of what you're carrying.  Some objects have unexpected\neffects; the effects are not always desirable!  Usually people having\ntrouble moving just need to try a few more words.  Usually people\ntrying unsuccessfully to manipulate an object are attempting something\nbeyond their (or my!) capabilities and should try a completely\ndifferent tack.  One point often confusing to beginners is that, when\nthere are several ways to go in a certain direction (e.g., if there\nare several holes in a wall), choosing that direction in effect\nchooses one of the ways at random; often, though, by specifying the\nplace you want to reach you can guarantee choosing the right path.\nAlso, to speed the game you can sometimes move long distances with a\nsingle word.  For example, \"building\" usually gets you to the building\nfrom anywhere above ground except when lost in the forest.  Also, note\nthat cave passages and forest paths turn a lot, so leaving one place\nheading north doesn't guarantee entering the next from the south.\nHowever (another important point), except when you've used a \"long\ndistance\" word such as \"building\", there is always a way to go back\nwhere you just came from unless I warn you to the contrary, even\nthough the direction that takes you back might not be the reverse of\nwhat got you here.  Good luck, and have fun!", noAction = true), // HELP
    Action(words = listOf("no"), message = "OK", noAction = true), // False
    Action(words = listOf("tree", "trees"), message = "The trees of the forest are large hardwood oak and maple, with an\noccasional grove of pine or spruce.  There is quite a bit of under-\ngrowth, largely birch and ash saplings plus nondescript bushes of\nvarious sorts.  This time of year visibility is quite restricted by\nall the leaves, but travel is quite easy if you detour around the\nspruce and berry bushes.", noAction = true), // TREE
    Action(words = listOf("dig", "excav"), message = "Digging without a shovel is quite impractical.  Even with a shovel\nprogress is unlikely.", noAction = true), // DIG
    Action(words = listOf("lost"), message = "I'm as confused as you are.", noAction = true), // LOST
    Action(words = listOf("mist"), message = "Mist is a white vapor, usually water, seen from time to time in\ncaverns.  It can be found anywhere but is frequently a sign of a deep\npit leading down to water.", noAction = true), // MIST
    Action(words = listOf("fuck"), message = "Watch it!", noAction = true), // FBOMB
    Action(words = listOf("stop"), message = "I don't know the word \"stop\".  Use \"quit\" if you want to give up.", noAction = true), // STOP
    Action(words = listOf("info", "infor"), message = "For a summary of the most recent changes to the game, say \"news\".\nIf you want to end your adventure early, say \"quit\".  To suspend your\nadventure such that you can continue later, say \"suspend\" (or \"pause\"\nor \"save\").  To see how well you're doing, say \"score\".  To get full\ncredit for a treasure, you must have left it safely in the building,\nthough you get partial credit just for locating it.  You lose points\nfor getting killed, or for quitting, though the former costs you more.\nThere are also points based on how much (if any) of the cave you've\nmanaged to explore; in particular, there is a large bonus just for\ngetting in (to distinguish the beginners from the rest of the pack),\nand there are other ways to determine whether you've been through some\nof the more harrowing sections.  If you think you've found all the\ntreasures, just keep exploring for a while.  If nothing interesting\nhappens, you haven't found them all yet.  If something interesting\n*DOES* happen (incidentally, there *ARE* ways to hasten things along),\nit means you're getting a bonus and have an opportunity to garner many\nmore points in the Master's section.  I may occasionally offer hints\nif you seem to be having trouble.  If I do, I'll warn you in advance\nhow much it will affect your score to accept the hints.  Finally, to\nsave time, you may specify \"brief\", which tells me never to repeat the\nfull description of a place unless you explicitly ask me to.", noAction = true), // INFO
    Action(words = listOf("swim"), message = "I don't know how.", noAction = true), // SWIM
    Action(words = listOf("wizar"), message = "Wizards are not to be disturbed by such as you.", noAction = true), // WIZARD
    Action(words = listOf("yes"), message = "Guess again.", noAction = true), // YES
    Action(words = listOf("news"), message = "Open Adventure is an author-approved open-source release of\nVersion 2.5 with, as yet, no gameplay changes.\nVersion 2.5 was essentially the same as Version II; the cave and the\nhazards therein are unchanged, and top score is still 430 points.\nThere are a few more hints, especially for some of the more obscure\npuzzles.  There are a few minor bugfixes and cosmetic changes.  You\ncan now save a game and resume it at once (formerly you had to wait a\nwhile first), but it now costs you a few points each time you save the\ngame.  Saved games are now stored in much smaller files than before.", noAction = true), // NEWS
    Action(words = listOf("versi"), message = "There is a puff of orange smoke; within it, fiery runes spell out:\n\nOpen Adventure %V - http://www.catb.org/esr/open-adventure/", noAction = true), // ACT_VERSION
)

val travel: Array<TravelOp> = arrayOf(
    TravelOp(motion = 0, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = 0, noDwarves = false, stop = false), // from LOC_NOWHERE
    TravelOp(motion = MOT_2, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = ENTER, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BUILDING, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = MOT_12, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BUILDING, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = INSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BUILDING, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BUILDING, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = MOT_5, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = MOT_13, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = STREAM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = MOT_6, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST1, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST1, noDwarves = false, stop = false), // from LOC_START
    TravelOp(motion = DEPRESSION, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = true), // from LOC_START
    TravelOp(motion = MOT_12, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_HILL
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_HILL
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ROADEND, noDwarves = false, stop = false), // from LOC_HILL
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST20, noDwarves = false, stop = false), // from LOC_HILL
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST13, noDwarves = false, stop = false), // from LOC_HILL
    TravelOp(motion = MOT_6, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST13, noDwarves = false, stop = false), // from LOC_HILL
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = WHICH_WAY, noDwarves = false, stop = true), // from LOC_HILL
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_BUILDING
    TravelOp(motion = MOT_32, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_BUILDING
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_BUILDING
    TravelOp(motion = XYZZY, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOOF1, noDwarves = false, stop = false), // from LOC_BUILDING
    TravelOp(motion = PLUGH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOOF3, noDwarves = false, stop = false), // from LOC_BUILDING
    TravelOp(motion = MOT_5, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SEWER, noDwarves = false, stop = false), // from LOC_BUILDING
    TravelOp(motion = STREAM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SEWER, noDwarves = false, stop = true), // from LOC_BUILDING
    TravelOp(motion = MOT_4, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = MOT_12, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST6, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = MOT_6, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST6, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST12, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = MOT_5, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLIT, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLIT, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLIT, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = DEPRESSION, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_VALLEY
    TravelOp(motion = STREAM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = UPSTREAM_DOWNSTREAM, noDwarves = false, stop = true), // from LOC_VALLEY
    TravelOp(motion = MOT_2, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_ROADEND
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_ROADEND
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_ROADEND
    TravelOp(motion = MOT_12, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_ROADEND
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST14, noDwarves = false, stop = false), // from LOC_ROADEND
    TravelOp(motion = MOT_6, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST14, noDwarves = false, stop = false), // from LOC_ROADEND
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST15, noDwarves = false, stop = false), // from LOC_ROADEND
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST21, noDwarves = false, stop = true), // from LOC_ROADEND
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST17, noDwarves = false, stop = false), // from LOC_CLIFF
    TravelOp(motion = MOT_6, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST17, noDwarves = false, stop = false), // from LOC_CLIFF
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST19, noDwarves = false, stop = false), // from LOC_CLIFF
    TravelOp(motion = MOT_39, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NOMAKE, noDwarves = false, stop = true), // from LOC_CLIFF
    TravelOp(motion = MOT_12, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = MOT_4, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST6, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = MOT_6, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST6, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST10, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = MOT_5, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = MOT_16, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = DEPRESSION, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = MOT_60, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = STREAM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = INSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SLIT
    TravelOp(motion = ENTER, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = true), // from LOC_SLIT
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST7, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = MOT_6, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST7, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST10, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST9, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = MOT_12, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = MOT_4, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLIT, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = MOT_13, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLIT, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLIT, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = ENTER, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = INSIDE, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = DOWN, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_GRATE
    TravelOp(motion = ENTER, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = GRATE_NOWAY, noDwarves = false, stop = true), // from LOC_GRATE
    TravelOp(motion = OUTSIDE, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_BELOWGRATE
    TravelOp(motion = UP, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_BELOWGRATE
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = GRATE_NOWAY, noDwarves = false, stop = false), // from LOC_BELOWGRATE
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_BELOWGRATE
    TravelOp(motion = MOT_18, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_BELOWGRATE
    TravelOp(motion = INSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_BELOWGRATE
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_BELOWGRATE
    TravelOp(motion = MOT_31, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = false), // from LOC_BELOWGRATE
    TravelOp(motion = MOT_51, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = true), // from LOC_BELOWGRATE
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_COBBLE
    TravelOp(motion = MOT_20, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_COBBLE
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_COBBLE
    TravelOp(motion = INSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_COBBLE
    TravelOp(motion = MOT_22, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_COBBLE
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_COBBLE
    TravelOp(motion = MOT_51, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_COBBLE
    TravelOp(motion = MOT_31, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = true), // from LOC_COBBLE
    TravelOp(motion = DEPRESSION, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = ENTRANCE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = MOT_18, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = MOT_24, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COBBLE, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = MOT_25, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_AWKWARD, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = INSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_AWKWARD, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_AWKWARD, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_AWKWARD, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = XYZZY, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOOF2, noDwarves = false, stop = false), // from LOC_DEBRIS
    TravelOp(motion = MOT_31, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = true), // from LOC_DEBRIS
    TravelOp(motion = DEPRESSION, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_AWKWARD
    TravelOp(motion = ENTRANCE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_AWKWARD
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_AWKWARD
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_AWKWARD
    TravelOp(motion = MOT_51, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_AWKWARD
    TravelOp(motion = INSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BIRDCHAMBER, noDwarves = false, stop = false), // from LOC_AWKWARD
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BIRDCHAMBER, noDwarves = false, stop = false), // from LOC_AWKWARD
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BIRDCHAMBER, noDwarves = false, stop = false), // from LOC_AWKWARD
    TravelOp(motion = MOT_31, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = true), // from LOC_AWKWARD
    TravelOp(motion = DEPRESSION, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_BIRDCHAMBER
    TravelOp(motion = ENTRANCE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_BIRDCHAMBER
    TravelOp(motion = MOT_51, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_BIRDCHAMBER
    TravelOp(motion = MOT_25, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_AWKWARD, noDwarves = false, stop = false), // from LOC_BIRDCHAMBER
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_AWKWARD, noDwarves = false, stop = false), // from LOC_BIRDCHAMBER
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = false), // from LOC_BIRDCHAMBER
    TravelOp(motion = MOT_31, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = false), // from LOC_BIRDCHAMBER
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = true), // from LOC_BIRDCHAMBER
    TravelOp(motion = DEPRESSION, condType = CondType.NOT, condArg1 = 3, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = ENTRANCE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BELOWGRATE, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = MOT_51, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BIRDCHAMBER, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BIRDCHAMBER, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = DOWN, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NECKBROKE, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = MOT_31, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NECKBROKE, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = MOT_34, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NECKBROKE, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = MOT_33, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CRACK, noDwarves = false, stop = false), // from LOC_PITTOP
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CRACK, noDwarves = false, stop = true), // from LOC_PITTOP
    TravelOp(motion = LEFT, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NUGGET, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NUGGET, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = FORWARD, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTBANK, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = MOT_38, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTBANK, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTBANK, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = MOT_10, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = UP, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DOME, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = MOT_31, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DOME, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = MOT_34, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DOME, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = MOT_35, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DOME, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = MOT_23, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DOME, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = EAST, condType = CondType.CARRY, condArg1 = NUGGET, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DOME, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = false), // from LOC_MISTHALL
    TravelOp(motion = MOT_55, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_JUMBLE, noDwarves = false, stop = true), // from LOC_MISTHALL
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITTOP, noDwarves = false, stop = true), // from LOC_CRACK
    TravelOp(motion = MOT_38, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = false), // from LOC_EASTBANK
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = false), // from LOC_EASTBANK
    TravelOp(motion = MOT_39, condType = CondType.NOT, condArg1 = 12, condArg2 = 0, destType = DestType.SPEAK, destVal = CROSS_BRIDGE, noDwarves = false, stop = false), // from LOC_EASTBANK
    TravelOp(motion = FORWARD, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.GOTO, destVal = LOC_NOMAKE, noDwarves = false, stop = false), // from LOC_EASTBANK
    TravelOp(motion = MOT_41, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.SPEAK, destVal = NO_CROSS, noDwarves = false, stop = false), // from LOC_EASTBANK
    TravelOp(motion = MOT_42, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.SPEAK, destVal = NO_CROSS, noDwarves = false, stop = false), // from LOC_EASTBANK
    TravelOp(motion = WEST, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.SPEAK, destVal = NO_CROSS, noDwarves = false, stop = false), // from LOC_EASTBANK
    TravelOp(motion = CROSS, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.SPEAK, destVal = NO_CROSS, noDwarves = false, stop = false), // from LOC_EASTBANK
    TravelOp(motion = MOT_41, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTBANK, noDwarves = false, stop = true), // from LOC_EASTBANK
    TravelOp(motion = MOT_38, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = false), // from LOC_NUGGET
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = false), // from LOC_NUGGET
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = true), // from LOC_NUGGET
    TravelOp(motion = MOT_10, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = NORTH, condType = CondType.NOT, condArg1 = 11, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FLOORHOLE, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = RIGHT, condType = CondType.NOT, condArg1 = 11, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FLOORHOLE, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = SOUTH, condType = CondType.NOT, condArg1 = 11, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SOUTHSIDE, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = LEFT, condType = CondType.NOT, condArg1 = 11, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SOUTHSIDE, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = WEST, condType = CondType.NOT, condArg1 = 11, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTSIDE, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = FORWARD, condType = CondType.NOT, condArg1 = 11, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTSIDE, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SNAKEBLOCK, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = SW, condType = CondType.PCT, condArg1 = 35, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET3, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = SW, condType = CondType.WITH, condArg1 = SNAKE, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SNAKEBLOCK, noDwarves = false, stop = false), // from LOC_KINGHALL
    TravelOp(motion = MOT_66, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET3, noDwarves = false, stop = true), // from LOC_KINGHALL
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NOWHERE, noDwarves = false, stop = true), // from LOC_NECKBROKE
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NOWHERE, noDwarves = false, stop = true), // from LOC_NOMAKE
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = true), // from LOC_DOME
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTEND, noDwarves = false, stop = false), // from LOC_WESTEND
    TravelOp(motion = MOT_42, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTEND, noDwarves = false, stop = false), // from LOC_WESTEND
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLAB, noDwarves = false, stop = false), // from LOC_WESTEND
    TravelOp(motion = MOT_61, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLAB, noDwarves = false, stop = false), // from LOC_WESTEND
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTPIT, noDwarves = false, stop = false), // from LOC_WESTEND
    TravelOp(motion = MOT_31, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTPIT, noDwarves = false, stop = false), // from LOC_WESTEND
    TravelOp(motion = MOT_52, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = TOO_FAR, noDwarves = false, stop = true), // from LOC_WESTEND
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTEND, noDwarves = false, stop = false), // from LOC_EASTPIT
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTEND, noDwarves = false, stop = true), // from LOC_EASTPIT
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTEND, noDwarves = false, stop = false), // from LOC_WESTPIT
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTEND, noDwarves = false, stop = false), // from LOC_WESTPIT
    TravelOp(motion = MOT_56, condType = CondType.NOT, condArg1 = 24, condArg2 = 2, destType = DestType.GOTO, destVal = LOC_BUILDING1, noDwarves = false, stop = false), // from LOC_WESTPIT
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIMBSTALK, noDwarves = false, stop = true), // from LOC_WESTPIT
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NARROW, noDwarves = false, stop = true), // from LOC_CLIMBSTALK
    TravelOp(motion = MOT_39, condType = CondType.NOT, condArg1 = 12, condArg2 = 0, destType = DestType.SPEAK, destVal = CROSS_BRIDGE, noDwarves = false, stop = false), // from LOC_WESTBANK
    TravelOp(motion = FORWARD, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.GOTO, destVal = LOC_NOMAKE, noDwarves = false, stop = false), // from LOC_WESTBANK
    TravelOp(motion = MOT_41, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.SPEAK, destVal = NO_CROSS, noDwarves = false, stop = false), // from LOC_WESTBANK
    TravelOp(motion = MOT_42, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.SPEAK, destVal = NO_CROSS, noDwarves = false, stop = false), // from LOC_WESTBANK
    TravelOp(motion = EAST, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.SPEAK, destVal = NO_CROSS, noDwarves = false, stop = false), // from LOC_WESTBANK
    TravelOp(motion = CROSS, condType = CondType.NOT, condArg1 = 12, condArg2 = 1, destType = DestType.SPEAK, destVal = NO_CROSS, noDwarves = false, stop = false), // from LOC_WESTBANK
    TravelOp(motion = MOT_41, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTBANK, noDwarves = false, stop = false), // from LOC_WESTBANK
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PARALLEL1, noDwarves = false, stop = false), // from LOC_WESTBANK
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTWEST, noDwarves = false, stop = true), // from LOC_WESTBANK
    TravelOp(motion = MOT_38, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_FLOORHOLE
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_FLOORHOLE
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_FLOORHOLE
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_Y2, noDwarves = false, stop = false), // from LOC_FLOORHOLE
    TravelOp(motion = MOT_55, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_Y2, noDwarves = false, stop = false), // from LOC_FLOORHOLE
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BROKEN, noDwarves = false, stop = false), // from LOC_FLOORHOLE
    TravelOp(motion = MOT_52, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BROKEN, noDwarves = false, stop = true), // from LOC_FLOORHOLE
    TravelOp(motion = MOT_38, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_SOUTHSIDE
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_SOUTHSIDE
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = true), // from LOC_SOUTHSIDE
    TravelOp(motion = MOT_38, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_WESTSIDE
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_WESTSIDE
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_WESTSIDE
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CROSSOVER, noDwarves = false, stop = false), // from LOC_WESTSIDE
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CROSSOVER, noDwarves = false, stop = true), // from LOC_WESTSIDE
    TravelOp(motion = 1, condType = CondType.NOT, condArg1 = 24, condArg2 = 1, destType = DestType.GOTO, destVal = LOC_NOCLIMB, noDwarves = false, stop = false), // from LOC_BUILDING1
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PLANTTOP, noDwarves = false, stop = true), // from LOC_BUILDING1
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = true), // from LOC_SNAKEBLOCK
    TravelOp(motion = PLUGH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOOF4, noDwarves = false, stop = false), // from LOC_Y2
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FLOORHOLE, noDwarves = false, stop = false), // from LOC_Y2
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_JUMBLE, noDwarves = false, stop = false), // from LOC_Y2
    TravelOp(motion = MOT_53, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_JUMBLE, noDwarves = false, stop = false), // from LOC_Y2
    TravelOp(motion = MOT_54, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_JUMBLE, noDwarves = false, stop = false), // from LOC_Y2
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WINDOW1, noDwarves = false, stop = false), // from LOC_Y2
    TravelOp(motion = PLOVER, condType = CondType.CARRY, condArg1 = EMERALD, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_Y2
    TravelOp(motion = PLOVER, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOOF5, noDwarves = false, stop = true), // from LOC_Y2
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_Y2, noDwarves = false, stop = false), // from LOC_JUMBLE
    TravelOp(motion = MOT_55, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_Y2, noDwarves = false, stop = false), // from LOC_JUMBLE
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTHALL, noDwarves = false, stop = true), // from LOC_JUMBLE
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_Y2, noDwarves = false, stop = false), // from LOC_WINDOW1
    TravelOp(motion = MOT_55, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_Y2, noDwarves = false, stop = false), // from LOC_WINDOW1
    TravelOp(motion = MOT_39, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NECKBROKE, noDwarves = false, stop = true), // from LOC_WINDOW1
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SMALLPITBRINK, noDwarves = false, stop = false), // from LOC_BROKEN
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SMALLPITBRINK, noDwarves = false, stop = false), // from LOC_BROKEN
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FLOORHOLE, noDwarves = false, stop = false), // from LOC_BROKEN
    TravelOp(motion = MOT_52, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FLOORHOLE, noDwarves = false, stop = false), // from LOC_BROKEN
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DUSTY, noDwarves = false, stop = false), // from LOC_BROKEN
    TravelOp(motion = BEDQUILT, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = true), // from LOC_BROKEN
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BROKEN, noDwarves = false, stop = false), // from LOC_SMALLPITBRINK
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BROKEN, noDwarves = false, stop = false), // from LOC_SMALLPITBRINK
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SMALLPIT, noDwarves = false, stop = false), // from LOC_SMALLPITBRINK
    TravelOp(motion = MOT_31, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SMALLPIT, noDwarves = false, stop = false), // from LOC_SMALLPITBRINK
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SMALLPIT, noDwarves = false, stop = true), // from LOC_SMALLPITBRINK
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SMALLPITBRINK, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SMALLPITBRINK, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SMALLPITBRINK, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = MOT_60, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = STREAM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = MOT_4, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = MOT_5, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = ENTER, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = false), // from LOC_SMALLPIT
    TravelOp(motion = INSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = DONT_FIT, noDwarves = false, stop = true), // from LOC_SMALLPIT
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BROKEN, noDwarves = false, stop = false), // from LOC_DUSTY
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BROKEN, noDwarves = false, stop = false), // from LOC_DUSTY
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COMPLEX, noDwarves = false, stop = false), // from LOC_DUSTY
    TravelOp(motion = MOT_52, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COMPLEX, noDwarves = false, stop = false), // from LOC_DUSTY
    TravelOp(motion = MOT_58, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COMPLEX, noDwarves = false, stop = false), // from LOC_DUSTY
    TravelOp(motion = BEDQUILT, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = true), // from LOC_DUSTY
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTWEST, noDwarves = false, stop = true), // from LOC_PARALLEL1
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE1, noDwarves = false, stop = false), // from LOC_MISTWEST
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE1, noDwarves = false, stop = false), // from LOC_MISTWEST
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE1, noDwarves = false, stop = false), // from LOC_MISTWEST
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE1, noDwarves = false, stop = false), // from LOC_MISTWEST
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTBANK, noDwarves = false, stop = false), // from LOC_MISTWEST
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PARALLEL2, noDwarves = false, stop = false), // from LOC_MISTWEST
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LONGEAST, noDwarves = false, stop = false), // from LOC_MISTWEST
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LONGEAST, noDwarves = false, stop = true), // from LOC_MISTWEST
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTWEST, noDwarves = false, stop = false), // from LOC_ALIKE1
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE1, noDwarves = false, stop = false), // from LOC_ALIKE1
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE2, noDwarves = false, stop = false), // from LOC_ALIKE1
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = false), // from LOC_ALIKE1
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE11, noDwarves = false, stop = true), // from LOC_ALIKE1
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE1, noDwarves = false, stop = false), // from LOC_ALIKE2
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE3, noDwarves = false, stop = false), // from LOC_ALIKE2
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = true), // from LOC_ALIKE2
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE2, noDwarves = false, stop = false), // from LOC_ALIKE3
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND3, noDwarves = false, stop = false), // from LOC_ALIKE3
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE6, noDwarves = false, stop = false), // from LOC_ALIKE3
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND9, noDwarves = false, stop = true), // from LOC_ALIKE3
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE1, noDwarves = false, stop = false), // from LOC_ALIKE4
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE2, noDwarves = false, stop = false), // from LOC_ALIKE4
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND1, noDwarves = false, stop = false), // from LOC_ALIKE4
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND2, noDwarves = false, stop = false), // from LOC_ALIKE4
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE14, noDwarves = false, stop = false), // from LOC_ALIKE4
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE14, noDwarves = false, stop = true), // from LOC_ALIKE4
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = false), // from LOC_MAZEEND1
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = true), // from LOC_MAZEEND1
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = false), // from LOC_MAZEEND2
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = true), // from LOC_MAZEEND2
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE3, noDwarves = false, stop = false), // from LOC_MAZEEND3
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE3, noDwarves = false, stop = true), // from LOC_MAZEEND3
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE6, noDwarves = false, stop = false), // from LOC_ALIKE5
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE7, noDwarves = false, stop = true), // from LOC_ALIKE5
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE3, noDwarves = false, stop = false), // from LOC_ALIKE6
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE5, noDwarves = false, stop = false), // from LOC_ALIKE6
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE7, noDwarves = false, stop = false), // from LOC_ALIKE6
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE8, noDwarves = false, stop = true), // from LOC_ALIKE6
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE5, noDwarves = false, stop = false), // from LOC_ALIKE7
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE6, noDwarves = false, stop = false), // from LOC_ALIKE7
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE8, noDwarves = false, stop = false), // from LOC_ALIKE7
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE9, noDwarves = false, stop = true), // from LOC_ALIKE7
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE6, noDwarves = false, stop = false), // from LOC_ALIKE8
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE7, noDwarves = false, stop = false), // from LOC_ALIKE8
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE8, noDwarves = false, stop = false), // from LOC_ALIKE8
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE9, noDwarves = false, stop = false), // from LOC_ALIKE8
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE10, noDwarves = false, stop = false), // from LOC_ALIKE8
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND11, noDwarves = false, stop = true), // from LOC_ALIKE8
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE7, noDwarves = false, stop = false), // from LOC_ALIKE9
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE8, noDwarves = false, stop = false), // from LOC_ALIKE9
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND4, noDwarves = false, stop = true), // from LOC_ALIKE9
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE9, noDwarves = false, stop = false), // from LOC_MAZEEND4
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE9, noDwarves = false, stop = true), // from LOC_MAZEEND4
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE8, noDwarves = false, stop = false), // from LOC_ALIKE10
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE10, noDwarves = false, stop = false), // from LOC_ALIKE10
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND5, noDwarves = false, stop = false), // from LOC_ALIKE10
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITBRINK, noDwarves = false, stop = true), // from LOC_ALIKE10
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE10, noDwarves = false, stop = false), // from LOC_MAZEEND5
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE10, noDwarves = false, stop = true), // from LOC_MAZEEND5
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BIRDCHAMBER, noDwarves = false, stop = false), // from LOC_PITBRINK
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BIRDCHAMBER, noDwarves = false, stop = false), // from LOC_PITBRINK
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE10, noDwarves = false, stop = false), // from LOC_PITBRINK
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND6, noDwarves = false, stop = false), // from LOC_PITBRINK
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE12, noDwarves = false, stop = false), // from LOC_PITBRINK
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE13, noDwarves = false, stop = true), // from LOC_PITBRINK
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITBRINK, noDwarves = false, stop = false), // from LOC_MAZEEND6
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITBRINK, noDwarves = false, stop = true), // from LOC_MAZEEND6
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTBANK, noDwarves = false, stop = true), // from LOC_PARALLEL2
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTWEST, noDwarves = false, stop = false), // from LOC_LONGEAST
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTWEST, noDwarves = false, stop = false), // from LOC_LONGEAST
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTWEST, noDwarves = false, stop = false), // from LOC_LONGEAST
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LONGWEST, noDwarves = false, stop = false), // from LOC_LONGEAST
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CROSSOVER, noDwarves = false, stop = false), // from LOC_LONGEAST
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CROSSOVER, noDwarves = false, stop = false), // from LOC_LONGEAST
    TravelOp(motion = MOT_52, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CROSSOVER, noDwarves = false, stop = true), // from LOC_LONGEAST
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LONGEAST, noDwarves = false, stop = false), // from LOC_LONGWEST
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CROSSOVER, noDwarves = false, stop = false), // from LOC_LONGWEST
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 100, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = true, stop = true), // from LOC_LONGWEST
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LONGEAST, noDwarves = false, stop = false), // from LOC_CROSSOVER
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEADEND7, noDwarves = false, stop = false), // from LOC_CROSSOVER
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTSIDE, noDwarves = false, stop = false), // from LOC_CROSSOVER
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LONGWEST, noDwarves = false, stop = true), // from LOC_CROSSOVER
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CROSSOVER, noDwarves = false, stop = false), // from LOC_DEADEND7
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CROSSOVER, noDwarves = false, stop = true), // from LOC_DEADEND7
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DUSTY, noDwarves = false, stop = false), // from LOC_COMPLEX
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DUSTY, noDwarves = false, stop = false), // from LOC_COMPLEX
    TravelOp(motion = MOT_59, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DUSTY, noDwarves = false, stop = false), // from LOC_COMPLEX
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = false), // from LOC_COMPLEX
    TravelOp(motion = BEDQUILT, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = false), // from LOC_COMPLEX
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SHELLROOM, noDwarves = false, stop = false), // from LOC_COMPLEX
    TravelOp(motion = SHELLROOM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SHELLROOM, noDwarves = false, stop = false), // from LOC_COMPLEX
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ANTEROOM, noDwarves = false, stop = true), // from LOC_COMPLEX
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COMPLEX, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SWISSCHEESE, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = SOUTH, condType = CondType.PCT, condArg1 = 65, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = MOT_61, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLAB, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = UP, condType = CondType.PCT, condArg1 = 60, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = UP, condType = CondType.PCT, condArg1 = 70, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET2, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DUSTY, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = NORTH, condType = CondType.PCT, condArg1 = 50, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = NORTH, condType = CondType.PCT, condArg1 = 75, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_THREEJUNCTION, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = DOWN, condType = CondType.PCT, condArg1 = 65, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_BEDQUILT
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ANTEROOM, noDwarves = false, stop = true), // from LOC_BEDQUILT
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = false), // from LOC_SWISSCHEESE
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTEND, noDwarves = false, stop = false), // from LOC_SWISSCHEESE
    TravelOp(motion = SOUTH, condType = CondType.PCT, condArg1 = 80, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_SWISSCHEESE
    TravelOp(motion = MOT_25, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TALL, noDwarves = false, stop = false), // from LOC_SWISSCHEESE
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SOFTROOM, noDwarves = false, stop = false), // from LOC_SWISSCHEESE
    TravelOp(motion = NW, condType = CondType.PCT, condArg1 = 50, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_SWISSCHEESE
    TravelOp(motion = ORIENTAL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ORIENTAL, noDwarves = false, stop = true), // from LOC_SWISSCHEESE
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SWISSCHEESE, noDwarves = false, stop = false), // from LOC_EASTEND
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTEND, noDwarves = false, stop = false), // from LOC_EASTEND
    TravelOp(motion = MOT_42, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTEND, noDwarves = false, stop = false), // from LOC_EASTEND
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTPIT, noDwarves = false, stop = false), // from LOC_EASTEND
    TravelOp(motion = MOT_31, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_EASTPIT, noDwarves = false, stop = true), // from LOC_EASTEND
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTEND, noDwarves = false, stop = false), // from LOC_SLAB
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET1, noDwarves = false, stop = false), // from LOC_SLAB
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET1, noDwarves = false, stop = false), // from LOC_SLAB
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = true), // from LOC_SLAB
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLAB, noDwarves = false, stop = false), // from LOC_SECRET1
    TravelOp(motion = MOT_61, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLAB, noDwarves = false, stop = false), // from LOC_SECRET1
    TravelOp(motion = SOUTH, condType = CondType.NOT, condArg1 = 31, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET5, noDwarves = false, stop = false), // from LOC_SECRET1
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET4, noDwarves = false, stop = false), // from LOC_SECRET1
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MIRRORCANYON, noDwarves = false, stop = false), // from LOC_SECRET1
    TravelOp(motion = RESERVOIR, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESERVOIR, noDwarves = false, stop = true), // from LOC_SECRET1
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_THREEJUNCTION, noDwarves = false, stop = false), // from LOC_SECRET2
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = false), // from LOC_SECRET2
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = false), // from LOC_SECRET2
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TOPSTALACTITE, noDwarves = false, stop = true), // from LOC_SECRET2
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = false), // from LOC_THREEJUNCTION
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET2, noDwarves = false, stop = false), // from LOC_THREEJUNCTION
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WINDOW2, noDwarves = false, stop = true), // from LOC_THREEJUNCTION
    TravelOp(motion = BEDQUILT, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = false), // from LOC_LOWROOM
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WINDING, noDwarves = false, stop = false), // from LOC_LOWROOM
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEADCRAWL, noDwarves = false, stop = false), // from LOC_LOWROOM
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ORIENTAL, noDwarves = false, stop = false), // from LOC_LOWROOM
    TravelOp(motion = ORIENTAL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ORIENTAL, noDwarves = false, stop = true), // from LOC_LOWROOM
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = false), // from LOC_DEADCRAWL
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = false), // from LOC_DEADCRAWL
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = true), // from LOC_DEADCRAWL
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_KINGHALL, noDwarves = false, stop = false), // from LOC_SECRET3
    TravelOp(motion = WEST, condType = CondType.NOT, condArg1 = 31, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET5, noDwarves = false, stop = false), // from LOC_SECRET3
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET6, noDwarves = false, stop = false), // from LOC_SECRET3
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WIDEPLACE, noDwarves = false, stop = true), // from LOC_SECRET3
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TIGHTPLACE, noDwarves = false, stop = false), // from LOC_WIDEPLACE
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TALL, noDwarves = false, stop = true), // from LOC_WIDEPLACE
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WIDEPLACE, noDwarves = false, stop = true), // from LOC_TIGHTPLACE
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WIDEPLACE, noDwarves = false, stop = false), // from LOC_TALL
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BOULDERS1, noDwarves = false, stop = false), // from LOC_TALL
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SWISSCHEESE, noDwarves = false, stop = false), // from LOC_TALL
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SWISSCHEESE, noDwarves = false, stop = true), // from LOC_TALL
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TALL, noDwarves = false, stop = true), // from LOC_BOULDERS1
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BUILDING, noDwarves = false, stop = true), // from LOC_SEWER
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE1, noDwarves = false, stop = false), // from LOC_ALIKE11
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE11, noDwarves = false, stop = false), // from LOC_ALIKE11
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE11, noDwarves = false, stop = false), // from LOC_ALIKE11
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND8, noDwarves = false, stop = true), // from LOC_ALIKE11
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE11, noDwarves = false, stop = false), // from LOC_MAZEEND8
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE11, noDwarves = false, stop = true), // from LOC_MAZEEND8
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE3, noDwarves = false, stop = false), // from LOC_MAZEEND9
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE3, noDwarves = false, stop = true), // from LOC_MAZEEND9
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITBRINK, noDwarves = false, stop = false), // from LOC_ALIKE12
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE13, noDwarves = false, stop = false), // from LOC_ALIKE12
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND10, noDwarves = false, stop = true), // from LOC_ALIKE12
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PITBRINK, noDwarves = false, stop = false), // from LOC_ALIKE13
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE12, noDwarves = false, stop = false), // from LOC_ALIKE13
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MAZEEND12, noDwarves = false, stop = true), // from LOC_ALIKE13
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE12, noDwarves = false, stop = false), // from LOC_MAZEEND10
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE12, noDwarves = false, stop = true), // from LOC_MAZEEND10
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE8, noDwarves = false, stop = false), // from LOC_MAZEEND11
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE8, noDwarves = false, stop = true), // from LOC_MAZEEND11
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = false), // from LOC_ALIKE14
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = true), // from LOC_ALIKE14
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTPIT, noDwarves = false, stop = false), // from LOC_NARROW
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTPIT, noDwarves = false, stop = false), // from LOC_NARROW
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTPIT, noDwarves = false, stop = false), // from LOC_NARROW
    TravelOp(motion = MOT_39, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NECKBROKE, noDwarves = false, stop = false), // from LOC_NARROW
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = false), // from LOC_NARROW
    TravelOp(motion = MOT_27, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = true), // from LOC_NARROW
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTPIT, noDwarves = false, stop = true), // from LOC_NOCLIMB
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WESTEND, noDwarves = false, stop = true), // from LOC_PLANTTOP
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WATERFALL, noDwarves = false, stop = false), // from LOC_INCLINE
    TravelOp(motion = CAVERN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WATERFALL, noDwarves = false, stop = false), // from LOC_INCLINE
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WATERFALL, noDwarves = false, stop = false), // from LOC_INCLINE
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = false), // from LOC_INCLINE
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = true), // from LOC_INCLINE
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NARROW, noDwarves = false, stop = false), // from LOC_GIANTROOM
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CAVEIN, noDwarves = false, stop = false), // from LOC_GIANTROOM
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_IMMENSE, noDwarves = false, stop = true), // from LOC_GIANTROOM
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = false), // from LOC_CAVEIN
    TravelOp(motion = MOT_27, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = false), // from LOC_CAVEIN
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = true), // from LOC_CAVEIN
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = false), // from LOC_IMMENSE
    TravelOp(motion = MOT_27, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = false), // from LOC_IMMENSE
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = false), // from LOC_IMMENSE
    TravelOp(motion = NORTH, condType = CondType.NOT, condArg1 = 9, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WATERFALL, noDwarves = false, stop = false), // from LOC_IMMENSE
    TravelOp(motion = ENTER, condType = CondType.NOT, condArg1 = 9, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WATERFALL, noDwarves = false, stop = false), // from LOC_IMMENSE
    TravelOp(motion = CAVERN, condType = CondType.NOT, condArg1 = 9, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WATERFALL, noDwarves = false, stop = false), // from LOC_IMMENSE
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = RUSTY_DOOR, noDwarves = false, stop = true), // from LOC_IMMENSE
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_IMMENSE, noDwarves = false, stop = false), // from LOC_WATERFALL
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_IMMENSE, noDwarves = false, stop = false), // from LOC_WATERFALL
    TravelOp(motion = MOT_27, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GIANTROOM, noDwarves = false, stop = false), // from LOC_WATERFALL
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_INCLINE, noDwarves = false, stop = true), // from LOC_WATERFALL
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SWISSCHEESE, noDwarves = false, stop = false), // from LOC_SOFTROOM
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SWISSCHEESE, noDwarves = false, stop = true), // from LOC_SOFTROOM
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SWISSCHEESE, noDwarves = false, stop = false), // from LOC_ORIENTAL
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = false), // from LOC_ORIENTAL
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = false), // from LOC_ORIENTAL
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTY, noDwarves = false, stop = false), // from LOC_ORIENTAL
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTY, noDwarves = false, stop = false), // from LOC_ORIENTAL
    TravelOp(motion = CAVERN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTY, noDwarves = false, stop = true), // from LOC_ORIENTAL
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ORIENTAL, noDwarves = false, stop = false), // from LOC_MISTY
    TravelOp(motion = ORIENTAL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ORIENTAL, noDwarves = false, stop = false), // from LOC_MISTY
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALCOVE, noDwarves = false, stop = true), // from LOC_MISTY
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTY, noDwarves = false, stop = false), // from LOC_ALCOVE
    TravelOp(motion = CAVERN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MISTY, noDwarves = false, stop = false), // from LOC_ALCOVE
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_ALCOVE
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_ALCOVE
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PLOVER, noDwarves = false, stop = true), // from LOC_ALCOVE
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_PLOVER
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_PLOVER
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_PLOVER
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALCOVE, noDwarves = false, stop = false), // from LOC_PLOVER
    TravelOp(motion = PLOVER, condType = CondType.CARRY, condArg1 = EMERALD, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_PLOVER
    TravelOp(motion = PLOVER, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOOF6, noDwarves = false, stop = false), // from LOC_PLOVER
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DARKROOM, noDwarves = false, stop = false), // from LOC_PLOVER
    TravelOp(motion = MOT_22, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DARKROOM, noDwarves = false, stop = true), // from LOC_PLOVER
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PLOVER, noDwarves = false, stop = false), // from LOC_DARKROOM
    TravelOp(motion = PLOVER, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PLOVER, noDwarves = false, stop = false), // from LOC_DARKROOM
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PLOVER, noDwarves = false, stop = true), // from LOC_DARKROOM
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SHELLROOM, noDwarves = false, stop = false), // from LOC_ARCHED
    TravelOp(motion = SHELLROOM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SHELLROOM, noDwarves = false, stop = false), // from LOC_ARCHED
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SHELLROOM, noDwarves = false, stop = true), // from LOC_ARCHED
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ARCHED, noDwarves = false, stop = false), // from LOC_SHELLROOM
    TravelOp(motion = MOT_38, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ARCHED, noDwarves = false, stop = false), // from LOC_SHELLROOM
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLOPING1, noDwarves = false, stop = false), // from LOC_SHELLROOM
    TravelOp(motion = SOUTH, condType = CondType.CARRY, condArg1 = CLAM, condArg2 = 0, destType = DestType.SPEAK, destVal = CLAM_BLOCKER, noDwarves = false, stop = false), // from LOC_SHELLROOM
    TravelOp(motion = SOUTH, condType = CondType.CARRY, condArg1 = OYSTER, condArg2 = 0, destType = DestType.SPEAK, destVal = OYSTER_BLOCKER, noDwarves = false, stop = false), // from LOC_SHELLROOM
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COMPLEX, noDwarves = false, stop = true), // from LOC_SHELLROOM
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SHELLROOM, noDwarves = false, stop = false), // from LOC_SLOPING1
    TravelOp(motion = SHELLROOM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SHELLROOM, noDwarves = false, stop = false), // from LOC_SLOPING1
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CULDESAC, noDwarves = false, stop = true), // from LOC_SLOPING1
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLOPING1, noDwarves = false, stop = false), // from LOC_CULDESAC
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLOPING1, noDwarves = false, stop = false), // from LOC_CULDESAC
    TravelOp(motion = SHELLROOM, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SHELLROOM, noDwarves = false, stop = true), // from LOC_CULDESAC
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_COMPLEX, noDwarves = false, stop = false), // from LOC_ANTEROOM
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BEDQUILT, noDwarves = false, stop = false), // from LOC_ANTEROOM
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WITTSEND, noDwarves = false, stop = true), // from LOC_ANTEROOM
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT1
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LONGWEST, noDwarves = false, stop = true), // from LOC_DIFFERENT1
    TravelOp(motion = EAST, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = NORTH, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = SOUTH, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = NE, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = SE, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = SW, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = NW, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = UP, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = DOWN, condType = CondType.PCT, condArg1 = 95, condArg2 = 0, destType = DestType.SPEAK, destVal = FUTILE_CRAWL, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ANTEROOM, noDwarves = false, stop = false), // from LOC_WITTSEND
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = WAY_BLOCKED, noDwarves = false, stop = true), // from LOC_WITTSEND
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET1, noDwarves = false, stop = false), // from LOC_MIRRORCANYON
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESERVOIR, noDwarves = false, stop = false), // from LOC_MIRRORCANYON
    TravelOp(motion = RESERVOIR, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESERVOIR, noDwarves = false, stop = true), // from LOC_MIRRORCANYON
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_THREEJUNCTION, noDwarves = false, stop = false), // from LOC_WINDOW2
    TravelOp(motion = MOT_39, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NECKBROKE, noDwarves = false, stop = true), // from LOC_WINDOW2
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET2, noDwarves = false, stop = false), // from LOC_TOPSTALACTITE
    TravelOp(motion = DOWN, condType = CondType.PCT, condArg1 = 40, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE6, noDwarves = false, stop = false), // from LOC_TOPSTALACTITE
    TravelOp(motion = MOT_39, condType = CondType.PCT, condArg1 = 40, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE6, noDwarves = false, stop = false), // from LOC_TOPSTALACTITE
    TravelOp(motion = MOT_56, condType = CondType.PCT, condArg1 = 40, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE6, noDwarves = false, stop = false), // from LOC_TOPSTALACTITE
    TravelOp(motion = DOWN, condType = CondType.PCT, condArg1 = 50, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE9, noDwarves = false, stop = false), // from LOC_TOPSTALACTITE
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE4, noDwarves = false, stop = true), // from LOC_TOPSTALACTITE
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT2
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEADEND13, noDwarves = false, stop = true), // from LOC_DIFFERENT2
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MIRRORCANYON, noDwarves = false, stop = false), // from LOC_RESERVOIR
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_MIRRORCANYON, noDwarves = false, stop = false), // from LOC_RESERVOIR
    TravelOp(motion = NORTH, condType = CondType.NOT, condArg1 = 45, condArg2 = 1, destType = DestType.SPEAK, destVal = BAD_DIRECTION, noDwarves = false, stop = false), // from LOC_RESERVOIR
    TravelOp(motion = MOT_42, condType = CondType.NOT, condArg1 = 45, condArg2 = 1, destType = DestType.SPEAK, destVal = BAD_DIRECTION, noDwarves = false, stop = false), // from LOC_RESERVOIR
    TravelOp(motion = CROSS, condType = CondType.NOT, condArg1 = 45, condArg2 = 1, destType = DestType.SPEAK, destVal = BAD_DIRECTION, noDwarves = false, stop = false), // from LOC_RESERVOIR
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESBOTTOM, noDwarves = false, stop = true), // from LOC_RESERVOIR
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ALIKE13, noDwarves = false, stop = true), // from LOC_MAZEEND12
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SW, noDwarves = false, stop = true), // from LOC_NE
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NE, noDwarves = false, stop = false), // from LOC_SW
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = GRATE_NOWAY, noDwarves = false, stop = true), // from LOC_SW
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WINDING, noDwarves = false, stop = false), // from LOC_SWCHASM
    TravelOp(motion = MOT_41, condType = CondType.WITH, condArg1 = TROLL, condArg2 = 0, destType = DestType.SPEAK, destVal = TROLL_BLOCKS, noDwarves = false, stop = false), // from LOC_SWCHASM
    TravelOp(motion = MOT_42, condType = CondType.WITH, condArg1 = TROLL, condArg2 = 0, destType = DestType.SPEAK, destVal = TROLL_BLOCKS, noDwarves = false, stop = false), // from LOC_SWCHASM
    TravelOp(motion = CROSS, condType = CondType.WITH, condArg1 = TROLL, condArg2 = 0, destType = DestType.SPEAK, destVal = TROLL_BLOCKS, noDwarves = false, stop = false), // from LOC_SWCHASM
    TravelOp(motion = NE, condType = CondType.WITH, condArg1 = TROLL, condArg2 = 0, destType = DestType.SPEAK, destVal = TROLL_BLOCKS, noDwarves = false, stop = false), // from LOC_SWCHASM
    TravelOp(motion = MOT_41, condType = CondType.NOT, condArg1 = 32, condArg2 = 0, destType = DestType.SPEAK, destVal = BRIDGE_GONE, noDwarves = false, stop = false), // from LOC_SWCHASM
    TravelOp(motion = MOT_41, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_BUILDING, noDwarves = false, stop = false), // from LOC_SWCHASM
    TravelOp(motion = MOT_39, condType = CondType.NOT, condArg1 = 32, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NOMAKE, noDwarves = false, stop = false), // from LOC_SWCHASM
    TravelOp(motion = MOT_39, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = CROSS_BRIDGE, noDwarves = false, stop = true), // from LOC_SWCHASM
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LOWROOM, noDwarves = false, stop = false), // from LOC_WINDING
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SWCHASM, noDwarves = false, stop = true), // from LOC_WINDING
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET1, noDwarves = false, stop = false), // from LOC_SECRET4
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET1, noDwarves = false, stop = false), // from LOC_SECRET4
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = NASTY_DRAGON, noDwarves = false, stop = false), // from LOC_SECRET4
    TravelOp(motion = FORWARD, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = NASTY_DRAGON, noDwarves = false, stop = true), // from LOC_SECRET4
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET1, noDwarves = false, stop = false), // from LOC_SECRET5
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET3, noDwarves = false, stop = true), // from LOC_SECRET5
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET3, noDwarves = false, stop = false), // from LOC_SECRET6
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SECRET3, noDwarves = false, stop = false), // from LOC_SECRET6
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = NASTY_DRAGON, noDwarves = false, stop = false), // from LOC_SECRET6
    TravelOp(motion = FORWARD, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = NASTY_DRAGON, noDwarves = false, stop = true), // from LOC_SECRET6
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CORRIDOR, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = MOT_41, condType = CondType.WITH, condArg1 = TROLL, condArg2 = 0, destType = DestType.SPEAK, destVal = TROLL_BLOCKS, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = MOT_42, condType = CondType.WITH, condArg1 = TROLL, condArg2 = 0, destType = DestType.SPEAK, destVal = TROLL_BLOCKS, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = CROSS, condType = CondType.WITH, condArg1 = TROLL, condArg2 = 0, destType = DestType.SPEAK, destVal = TROLL_BLOCKS, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = SW, condType = CondType.WITH, condArg1 = TROLL, condArg2 = 0, destType = DestType.SPEAK, destVal = TROLL_BLOCKS, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = MOT_41, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPECIAL, destVal = LOC_BUILDING, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = MOT_39, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = CROSS_BRIDGE, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = MOT_15, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = MOT_28, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = false), // from LOC_NECHASM
    TravelOp(motion = MOT_40, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENFRONT, noDwarves = false, stop = true), // from LOC_NECHASM
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NECHASM, noDwarves = false, stop = false), // from LOC_CORRIDOR
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_CORRIDOR
    TravelOp(motion = MOT_15, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_CORRIDOR
    TravelOp(motion = MOT_28, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = false), // from LOC_CORRIDOR
    TravelOp(motion = MOT_40, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENFRONT, noDwarves = false, stop = true), // from LOC_CORRIDOR
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CORRIDOR, noDwarves = false, stop = false), // from LOC_FORK
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WARMWALLS, noDwarves = false, stop = false), // from LOC_FORK
    TravelOp(motion = LEFT, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WARMWALLS, noDwarves = false, stop = false), // from LOC_FORK
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LIMESTONE, noDwarves = false, stop = false), // from LOC_FORK
    TravelOp(motion = RIGHT, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LIMESTONE, noDwarves = false, stop = false), // from LOC_FORK
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LIMESTONE, noDwarves = false, stop = false), // from LOC_FORK
    TravelOp(motion = MOT_28, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = false), // from LOC_FORK
    TravelOp(motion = MOT_40, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENFRONT, noDwarves = false, stop = true), // from LOC_FORK
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_WARMWALLS
    TravelOp(motion = MOT_15, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_WARMWALLS
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = false), // from LOC_WARMWALLS
    TravelOp(motion = MOT_28, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = false), // from LOC_WARMWALLS
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BOULDERS2, noDwarves = false, stop = false), // from LOC_WARMWALLS
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BOULDERS2, noDwarves = false, stop = true), // from LOC_WARMWALLS
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WARMWALLS, noDwarves = false, stop = false), // from LOC_BREATHTAKING
    TravelOp(motion = MOT_23, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WARMWALLS, noDwarves = false, stop = false), // from LOC_BREATHTAKING
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WARMWALLS, noDwarves = false, stop = false), // from LOC_BREATHTAKING
    TravelOp(motion = MOT_15, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_BREATHTAKING
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.SPEAK, destVal = RIDICULOUS_ATTEMPT, noDwarves = false, stop = false), // from LOC_BREATHTAKING
    TravelOp(motion = MOT_39, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRUESOME, noDwarves = false, stop = true), // from LOC_BREATHTAKING
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WARMWALLS, noDwarves = false, stop = false), // from LOC_BOULDERS2
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WARMWALLS, noDwarves = false, stop = false), // from LOC_BOULDERS2
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_WARMWALLS, noDwarves = false, stop = false), // from LOC_BOULDERS2
    TravelOp(motion = MOT_15, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_BOULDERS2
    TravelOp(motion = MOT_28, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = true), // from LOC_BOULDERS2
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_LIMESTONE
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_LIMESTONE
    TravelOp(motion = MOT_15, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_LIMESTONE
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENFRONT, noDwarves = false, stop = false), // from LOC_LIMESTONE
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENFRONT, noDwarves = false, stop = false), // from LOC_LIMESTONE
    TravelOp(motion = MOT_40, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENFRONT, noDwarves = false, stop = false), // from LOC_LIMESTONE
    TravelOp(motion = MOT_28, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = true), // from LOC_LIMESTONE
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LIMESTONE, noDwarves = false, stop = false), // from LOC_BARRENFRONT
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LIMESTONE, noDwarves = false, stop = false), // from LOC_BARRENFRONT
    TravelOp(motion = MOT_15, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_BARRENFRONT
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENROOM, noDwarves = false, stop = false), // from LOC_BARRENFRONT
    TravelOp(motion = INSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENROOM, noDwarves = false, stop = false), // from LOC_BARRENFRONT
    TravelOp(motion = MOT_40, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENROOM, noDwarves = false, stop = false), // from LOC_BARRENFRONT
    TravelOp(motion = ENTER, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENROOM, noDwarves = false, stop = false), // from LOC_BARRENFRONT
    TravelOp(motion = MOT_28, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = true), // from LOC_BARRENFRONT
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENFRONT, noDwarves = false, stop = false), // from LOC_BARRENROOM
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BARRENFRONT, noDwarves = false, stop = false), // from LOC_BARRENROOM
    TravelOp(motion = MOT_15, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FORK, noDwarves = false, stop = false), // from LOC_BARRENROOM
    TravelOp(motion = MOT_28, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BREATHTAKING, noDwarves = false, stop = true), // from LOC_BARRENROOM
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT3
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT3
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT4
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT4
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT5
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT5
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT6
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT6
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT7
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT7
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT8
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT8
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT9
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT9
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT11, noDwarves = false, stop = false), // from LOC_DIFFERENT10
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT10
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT1, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT3, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT4, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT5, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT6, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT7, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT8, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT9, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT10, noDwarves = false, stop = false), // from LOC_DIFFERENT11
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = true), // from LOC_DIFFERENT11
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = false), // from LOC_DEADEND13
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DIFFERENT2, noDwarves = false, stop = false), // from LOC_DEADEND13
    TravelOp(motion = SOUTH, condType = CondType.NOT, condArg1 = 38, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ROUGHHEWN, noDwarves = false, stop = false), // from LOC_DEADEND13
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BADDIRECTION, noDwarves = false, stop = true), // from LOC_DEADEND13
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEADEND13, noDwarves = false, stop = false), // from LOC_ROUGHHEWN
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LARGE, noDwarves = false, stop = true), // from LOC_ROUGHHEWN
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEADEND13, noDwarves = false, stop = true), // from LOC_BADDIRECTION
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ROUGHHEWN, noDwarves = false, stop = false), // from LOC_LARGE
    TravelOp(motion = NORTH, condType = CondType.WITH, condArg1 = OGRE, condArg2 = 0, destType = DestType.SPEAK, destVal = OGRE_SNARL, noDwarves = false, stop = false), // from LOC_LARGE
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_STOREROOM, noDwarves = false, stop = true), // from LOC_LARGE
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LARGE, noDwarves = false, stop = false), // from LOC_STOREROOM
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_LARGE, noDwarves = false, stop = true), // from LOC_STOREROOM
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_START, noDwarves = false, stop = false), // from LOC_FOREST1
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST13, noDwarves = false, stop = false), // from LOC_FOREST1
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST2, noDwarves = false, stop = false), // from LOC_FOREST1
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST3, noDwarves = false, stop = true), // from LOC_FOREST1
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST1, noDwarves = false, stop = false), // from LOC_FOREST2
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST19, noDwarves = false, stop = false), // from LOC_FOREST2
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST3, noDwarves = false, stop = false), // from LOC_FOREST2
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST18, noDwarves = false, stop = true), // from LOC_FOREST2
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST4, noDwarves = false, stop = false), // from LOC_FOREST3
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST4, noDwarves = false, stop = false), // from LOC_FOREST3
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST2, noDwarves = false, stop = false), // from LOC_FOREST3
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST1, noDwarves = false, stop = true), // from LOC_FOREST3
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST3, noDwarves = false, stop = false), // from LOC_FOREST4
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST3, noDwarves = false, stop = false), // from LOC_FOREST4
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST5, noDwarves = false, stop = false), // from LOC_FOREST4
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST5, noDwarves = false, stop = true), // from LOC_FOREST4
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST4, noDwarves = false, stop = false), // from LOC_FOREST5
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST4, noDwarves = false, stop = false), // from LOC_FOREST5
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST7, noDwarves = false, stop = false), // from LOC_FOREST5
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST6, noDwarves = false, stop = true), // from LOC_FOREST5
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST5, noDwarves = false, stop = false), // from LOC_FOREST6
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST7, noDwarves = false, stop = false), // from LOC_FOREST6
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = false), // from LOC_FOREST6
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLIT, noDwarves = false, stop = true), // from LOC_FOREST6
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST5, noDwarves = false, stop = false), // from LOC_FOREST7
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST6, noDwarves = false, stop = false), // from LOC_FOREST7
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = false), // from LOC_FOREST7
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST8, noDwarves = false, stop = true), // from LOC_FOREST7
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST9, noDwarves = false, stop = false), // from LOC_FOREST8
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST11, noDwarves = false, stop = false), // from LOC_FOREST8
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST22, noDwarves = false, stop = false), // from LOC_FOREST8
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST7, noDwarves = false, stop = true), // from LOC_FOREST8
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST11, noDwarves = false, stop = false), // from LOC_FOREST9
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST8, noDwarves = false, stop = false), // from LOC_FOREST9
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST10, noDwarves = false, stop = false), // from LOC_FOREST9
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = true), // from LOC_FOREST9
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_SLIT, noDwarves = false, stop = false), // from LOC_FOREST10
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST11, noDwarves = false, stop = false), // from LOC_FOREST10
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST9, noDwarves = false, stop = false), // from LOC_FOREST10
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_GRATE, noDwarves = false, stop = true), // from LOC_FOREST10
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST10, noDwarves = false, stop = false), // from LOC_FOREST11
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST8, noDwarves = false, stop = false), // from LOC_FOREST11
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST22, noDwarves = false, stop = false), // from LOC_FOREST11
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST9, noDwarves = false, stop = true), // from LOC_FOREST11
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST13, noDwarves = false, stop = false), // from LOC_FOREST12
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST14, noDwarves = false, stop = false), // from LOC_FOREST12
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST22, noDwarves = false, stop = false), // from LOC_FOREST12
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_VALLEY, noDwarves = false, stop = true), // from LOC_FOREST12
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST1, noDwarves = false, stop = false), // from LOC_FOREST13
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST12, noDwarves = false, stop = false), // from LOC_FOREST13
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST20, noDwarves = false, stop = false), // from LOC_FOREST13
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_HILL, noDwarves = false, stop = true), // from LOC_FOREST13
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ROADEND, noDwarves = false, stop = false), // from LOC_FOREST14
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST16, noDwarves = false, stop = false), // from LOC_FOREST14
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST15, noDwarves = false, stop = false), // from LOC_FOREST14
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST12, noDwarves = false, stop = true), // from LOC_FOREST14
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST16, noDwarves = false, stop = false), // from LOC_FOREST15
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST22, noDwarves = false, stop = false), // from LOC_FOREST15
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ROADEND, noDwarves = false, stop = false), // from LOC_FOREST15
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST14, noDwarves = false, stop = true), // from LOC_FOREST15
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST17, noDwarves = false, stop = false), // from LOC_FOREST16
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST17, noDwarves = false, stop = false), // from LOC_FOREST16
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST14, noDwarves = false, stop = false), // from LOC_FOREST16
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST15, noDwarves = false, stop = true), // from LOC_FOREST16
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST18, noDwarves = false, stop = false), // from LOC_FOREST17
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST16, noDwarves = false, stop = false), // from LOC_FOREST17
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST16, noDwarves = false, stop = false), // from LOC_FOREST17
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFF, noDwarves = false, stop = true), // from LOC_FOREST17
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST19, noDwarves = false, stop = false), // from LOC_FOREST18
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST17, noDwarves = false, stop = false), // from LOC_FOREST18
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST2, noDwarves = false, stop = false), // from LOC_FOREST18
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST21, noDwarves = false, stop = true), // from LOC_FOREST18
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST2, noDwarves = false, stop = false), // from LOC_FOREST19
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST18, noDwarves = false, stop = false), // from LOC_FOREST19
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFF, noDwarves = false, stop = false), // from LOC_FOREST19
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST20, noDwarves = false, stop = true), // from LOC_FOREST19
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_HILL, noDwarves = false, stop = false), // from LOC_FOREST20
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST21, noDwarves = false, stop = false), // from LOC_FOREST20
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST19, noDwarves = false, stop = false), // from LOC_FOREST20
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST13, noDwarves = false, stop = true), // from LOC_FOREST20
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST20, noDwarves = false, stop = false), // from LOC_FOREST21
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_ROADEND, noDwarves = false, stop = false), // from LOC_FOREST21
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST18, noDwarves = false, stop = false), // from LOC_FOREST21
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST21, noDwarves = false, stop = true), // from LOC_FOREST21
    TravelOp(motion = EAST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST8, noDwarves = false, stop = false), // from LOC_FOREST22
    TravelOp(motion = WEST, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST11, noDwarves = false, stop = false), // from LOC_FOREST22
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST15, noDwarves = false, stop = false), // from LOC_FOREST22
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOREST12, noDwarves = false, stop = true), // from LOC_FOREST22
    TravelOp(motion = MOT_39, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NOMAKE, noDwarves = false, stop = true), // from LOC_LEDGE
    TravelOp(motion = NORTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESNORTH, noDwarves = false, stop = false), // from LOC_RESBOTTOM
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESERVOIR, noDwarves = false, stop = true), // from LOC_RESBOTTOM
    TravelOp(motion = SOUTH, condType = CondType.NOT, condArg1 = 45, condArg2 = 1, destType = DestType.SPEAK, destVal = BAD_DIRECTION, noDwarves = false, stop = false), // from LOC_RESNORTH
    TravelOp(motion = MOT_42, condType = CondType.NOT, condArg1 = 45, condArg2 = 1, destType = DestType.SPEAK, destVal = BAD_DIRECTION, noDwarves = false, stop = false), // from LOC_RESNORTH
    TravelOp(motion = CROSS, condType = CondType.NOT, condArg1 = 45, condArg2 = 1, destType = DestType.SPEAK, destVal = BAD_DIRECTION, noDwarves = false, stop = false), // from LOC_RESNORTH
    TravelOp(motion = SOUTH, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESBOTTOM, noDwarves = false, stop = false), // from LOC_RESNORTH
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TREACHEROUS, noDwarves = false, stop = false), // from LOC_RESNORTH
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TREACHEROUS, noDwarves = false, stop = false), // from LOC_RESNORTH
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TREACHEROUS, noDwarves = false, stop = true), // from LOC_RESNORTH
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_STEEP, noDwarves = false, stop = false), // from LOC_TREACHEROUS
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_STEEP, noDwarves = false, stop = false), // from LOC_TREACHEROUS
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESNORTH, noDwarves = false, stop = false), // from LOC_TREACHEROUS
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_RESNORTH, noDwarves = false, stop = true), // from LOC_TREACHEROUS
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TREACHEROUS, noDwarves = false, stop = false), // from LOC_STEEP
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_TREACHEROUS, noDwarves = false, stop = false), // from LOC_STEEP
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFBASE, noDwarves = false, stop = false), // from LOC_STEEP
    TravelOp(motion = NW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFBASE, noDwarves = false, stop = true), // from LOC_STEEP
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_STEEP, noDwarves = false, stop = false), // from LOC_CLIFFBASE
    TravelOp(motion = SE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_STEEP, noDwarves = false, stop = false), // from LOC_CLIFFBASE
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFACE, noDwarves = false, stop = false), // from LOC_CLIFFBASE
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFACE, noDwarves = false, stop = true), // from LOC_CLIFFBASE
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFBASE, noDwarves = false, stop = false), // from LOC_CLIFFACE
    TravelOp(motion = UP, condType = CondType.CARRY, condArg1 = RABBITFOOT, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFTOP, noDwarves = false, stop = false), // from LOC_CLIFFACE
    TravelOp(motion = UP, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_FOOTSLIP, noDwarves = false, stop = true), // from LOC_CLIFFACE
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NOWHERE, noDwarves = false, stop = true), // from LOC_FOOTSLIP
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFLEDGE, noDwarves = false, stop = true), // from LOC_CLIFFTOP
    TravelOp(motion = MOT_56, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFACE, noDwarves = false, stop = false), // from LOC_CLIFFLEDGE
    TravelOp(motion = DOWN, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFACE, noDwarves = false, stop = false), // from LOC_CLIFFLEDGE
    TravelOp(motion = NE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_REACHDEAD, noDwarves = false, stop = false), // from LOC_CLIFFLEDGE
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_REACHDEAD, noDwarves = false, stop = true), // from LOC_CLIFFLEDGE
    TravelOp(motion = SW, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFLEDGE, noDwarves = false, stop = false), // from LOC_REACHDEAD
    TravelOp(motion = OUTSIDE, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFLEDGE, noDwarves = false, stop = false), // from LOC_REACHDEAD
    TravelOp(motion = CRAWL, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_CLIFFLEDGE, noDwarves = false, stop = true), // from LOC_REACHDEAD
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_NOWHERE, noDwarves = false, stop = true), // from LOC_GRUESOME
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_DEBRIS, noDwarves = false, stop = true), // from LOC_FOOF1
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BUILDING, noDwarves = false, stop = true), // from LOC_FOOF2
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_Y2, noDwarves = false, stop = true), // from LOC_FOOF3
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_BUILDING, noDwarves = false, stop = true), // from LOC_FOOF4
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_PLOVER, noDwarves = false, stop = true), // from LOC_FOOF5
    TravelOp(motion = 1, condType = CondType.GOTO, condArg1 = 0, condArg2 = 0, destType = DestType.GOTO, destVal = LOC_Y2, noDwarves = false, stop = true), // from LOC_FOOF6
)

val tkey: IntArray = intArrayOf(
    0, 1, 16, 23, 30, 41, 49, 53, 68, 80, 89, 97,
    110, 119, 127, 138, 154, 155, 164, 167, 180, 181, 182, 183,
    190, 192, 196, 197, 206, 213, 216, 221, 223, 224, 232, 235,
    238, 244, 249, 259, 265, 266, 274, 279, 282, 286, 292, 294,
    296, 298, 300, 304, 308, 314, 317, 319, 323, 325, 331, 333,
    334, 341, 344, 348, 350, 358, 370, 377, 382, 386, 392, 396,
    399, 404, 407, 411, 413, 414, 418, 419, 420, 424, 426, 428,
    431, 434, 436, 438, 440, 446, 447, 448, 453, 456, 459, 466,
    470, 472, 478, 481, 486, 494, 497, 500, 506, 509, 512, 515,
    525, 536, 539, 541, 547, 557, 563, 564, 565, 567, 576, 578,
    582, 584, 588, 598, 603, 611, 617, 623, 628, 635, 643, 647,
    657, 667, 677, 687, 697, 707, 717, 727, 737, 741, 743, 744,
    747, 749, 753, 757, 761, 765, 769, 773, 777, 781, 785, 789,
    793, 797, 801, 805, 809, 813, 817, 821, 825, 829, 833, 837,
    838, 840, 847, 851, 855, 859, 862, 863, 864, 868, 871, 872,
    873, 874, 875, 876, 877,
)
