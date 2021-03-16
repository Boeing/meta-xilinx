inherit features_check

REQUIRED_DISTRO_FEATURES = "video-common"

inherit esw

DEPENDS += "xilstandalone xilmem"

ESW_COMPONENT_SRC = "/XilinxProcessorIPLib/drivers/video_common/src/"
ESW_COMPONENT_NAME = "libvideo_common.a"
