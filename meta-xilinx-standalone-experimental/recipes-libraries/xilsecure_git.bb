inherit esw

ESW_COMPONENT_SRC = "/lib/sw_services/xilsecure/src/"
ESW_COMPONENT_NAME = "libxilsecure.a"

DEPENDS += "libxil xiltimer ${@'xilplmi' if d.getVar('ESW_MACHINE') == 'psv_pmc_0' else ''} ${@'xilmailbox' if d.getVar('MACHINE') == 'versal-generic' and d.getVar('ESW_MACHINE') != 'psv_pmc_0' else ''}"
