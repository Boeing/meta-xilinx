SUMMARY = "Control Software for VCU"
DESCRIPTION = "Control software libraries, test applications and headers provider for VCU"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=002a0a92906100955ea6ed02dcd2c2cd"

XILINX_VCU_VERSION = "1.0.0"
PV = "${XILINX_VCU_VERSION}-xilinx-v${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[1] or ''}+git${SRCPV}"

BRANCH ?= "xlnx_rel_v2024.1"
REPO   ?= "git://github.com/Xilinx/vcu-ctrl-sw.git;protocol=https"
SRCREV = "940f9fa933402de6f959911c236f36add5dd3a40"

BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH', True) != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

S  = "${WORKDIR}/git"

inherit features_check

REQUIRED_MACHINE_FEATURES = "vcu"

RDEPENDS:${PN} = "kernel-module-vcu"

EXTRA_OEMAKE = "CC='${CC}' CXX='${CXX} ${CXXFLAGS}'"

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}/vcu-ctrl-sw/include

    oe_runmake install_headers INSTALL_HDR_PATH=${D}${includedir}/vcu-ctrl-sw/include INSTALL_PATH=${D}/${bindir}
    oe_libinstall -C ${S}/bin/ -so liballegro_decode ${D}/${libdir}/
    oe_libinstall -C ${S}/bin/ -so liballegro_encode ${D}/${libdir}/
}

# These libraries shouldn't get installed in world builds unless something
# explicitly depends upon them.

EXCLUDE_FROM_WORLD = "1"

# Disable buildpaths QA check warnings.
INSANE_SKIP:${PN} += "buildpaths"