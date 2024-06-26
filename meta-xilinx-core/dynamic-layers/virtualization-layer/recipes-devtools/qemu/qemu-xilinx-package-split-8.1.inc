# we have our own package splitting for qemu, inhbit the oe-core
# split by overriding the split function
python split_qemu_packages () {
    print( "meta-virtualization: vmsplit: inhibiting core qemu package split" )
}

PACKAGES:prepend:class-target = "${PN}-x86_64 \
                     ${PN}-aarch64 \
                     ${PN}-arm \
                     ${PN}-i386 \
                     ${PN}-system-i386 \
                     ${PN}-microblaze \
                     ${PN}-support \
                     ${PN}-keymaps \
                     ${PN}-firmware \
                    "

FILES:${PN}-x86_64:class-target = "${bindir}/qemu-system-x86_64 ${bindir}/qemu-x86_64"
RDEPENDS:${PN}-x86_64:append:class-target = " ${PN}"
RPROVIDES:${PN}-x86_64:append:class-target = " ${PN}-system-x86_64"
RPROVIDES:${PN}-x86_64:append:class-target = " ${PN}-user-x86_64"
RDEPENDS:${PN}-system-all:append:class-target = " ${PN}-x86_64"
RDEPENDS:${PN}-user-all:append:class-target = " ${PN}-x86_64"
INSANE_SKIP:${PN}-x86_64:class-target = "file-rdeps"

FILES:${PN}-i386:class-target = "${bindir}/qemu-i386"
RDEPENDS:${PN}-i386:append:class-target = " ${PN}"
RPROVIDES:${PN}-i386:append:class-target = " ${PN}-user-i386"
RDEPENDS:${PN}-user-all:append:class-target = " ${PN}-i386"
INSANE_SKIP:${PN}-i386:class-target = "file-rdeps"

FILES:${PN}-system-i386:class-target = "${bindir}/qemu-system-i386"
RDEPENDS:${PN}-system-i386:append:class-target = " ${PN}"
RDEPENDS:${PN}-system-all:append:class-target = " ${PN}-system-i386"
INSANE_SKIP:${PN}-system-i386:class-target = "file-rdeps"

FILES:${PN}-aarch64:class-target = "${bindir}/qemu-system-aarch64 ${bindir}/qemu-aarch64"
RDEPENDS:${PN}-aarch64:append:class-target = " ${PN}"
RPROVIDES:${PN}-aarch64:append:class-target = " ${PN}-system-aarch64"
RPROVIDES:${PN}-aarch64:append:class-target = " ${PN}-user-aarch64"
RDEPENDS:${PN}-system-all:append:class-target = " ${PN}-aarch64"
RDEPENDS:${PN}-user-all:append:class-target = " ${PN}-aarch64"
INSANE_SKIP:${PN}-aarch64:class-target = "file-rdeps"

FILES:${PN}-arm:class-target = "${bindir}/qemu-system-arm ${bindir}/qemu-arm"
RDEPENDS:${PN}-arm:append:class-target = " ${PN}"
RPROVIDES:${PN}-arm:append:class-target = " ${PN}-system-arm"
RPROVIDES:${PN}-arm:append:class-target = " ${PN}-user-arm"
RDEPENDS:${PN}-system-all:append:class-target = " ${PN}-arm"
RDEPENDS:${PN}-user-all:append:class-target = " ${PN}-arm"
INSANE_SKIP:${PN}-arm:class-target = "file-rdeps"

FILES:${PN}-microblaze:class-target = "${bindir}/qemu-system-microblaze* ${bindir}/qemu-microblaze*"
RDEPENDS:${PN}-microblaze:append:class-target = " ${PN}"
RPROVIDES:${PN}-microblaze:append:class-target = " ${PN}-system-microblaze"
RPROVIDES:${PN}-microblaze:append:class-target = " ${PN}-user-microblaze"
RDEPENDS:${PN}-system-all:append:class-target = " ${PN}-microblaze"
RDEPENDS:${PN}-user-all:append:class-target = " ${PN}-microblaze"
INSANE_SKIP:${PN}-arm:class-target = "file-rdeps"

FILES:${PN}-support:class-target = "${bindir}/* ${libexecdir}/*"
RDEPENDS:${PN}-support:class-target = "${PN} bash"

FILES:${PN}-firmware:class-target = "${datadir}/${PN}/*.bin ${datadir}/${PN}/*.rom ${datadir}/${PN}/*.img ${datadir}/${PN}/openbios* ${datadir}/${PN}/*.dtb ${datadir}/${PN}/u-boot*"
RDEPENDS:${PN}-firmware:class-target = "${PN}"
INSANE_SKIP:${PN}-firmware:class-target = "arch"

FILES:${PN}-keymaps:class-target = "${datadir}/${PN}/keymaps/*"
RDEPENDS:${PN}-keymaps:class-target = "${PN}"

PACKAGECONFIG:append:class-target = " virtfs"
