
inherit features_check

REQUIRED_MACHINE_FEATURES = "trngpsv"

inherit esw python3native

DEPENDS += "xilstandalone "

ESW_COMPONENT_SRC = "/XilinxProcessorIPLib/drivers/trngpsv/src/"
ESW_COMPONENT_NAME = "libtrngpsv.a"

addtask do_generate_driver_data before do_configure after do_prepare_recipe_sysroot
do_prepare_recipe_sysroot[rdeptask] = "do_unpack"
