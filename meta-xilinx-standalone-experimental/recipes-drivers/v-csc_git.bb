inherit features_check

REQUIRED_DISTRO_FEATURES = "v-csc"

inherit esw python3native

DEPENDS += "xilstandalone xilmem video-common"

ESW_COMPONENT_SRC = "/XilinxProcessorIPLib/drivers/v_csc/src/"
ESW_COMPONENT_NAME = "libv_csc.a"

addtask do_generate_driver_data before do_configure after do_prepare_recipe_sysroot
do_prepare_recipe_sysroot[rdeptask] = "do_unpack"
