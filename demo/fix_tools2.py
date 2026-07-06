import sys

fp = "D:/JL/demo/src/main/java/com/example/demo/service/tool/ToolService.java"
with open(fp, "rb") as f:
    raw = f.read()
if raw[:3] == b"\xef\xbb\xbf":
    raw = raw[3:]
content = raw.decode("utf-8")

# 1. Fix imports - use wildcard for all pojos and services
old_imports = '''import com.example.demo.pojo.Resume;
import com.example.demo.pojo.User;
import com.example.demo.service.impl.ResumeServiceImpl;'''

new_imports = '''import com.example.demo.pojo.*;
import com.example.demo.service.*;
import com.example.demo.service.impl.ResumeServiceImpl;'''

content = content.replace(old_imports, new_imports)

# 2. Add @Autowired fields for other services after resumeService field
old_fields = '''    @Autowired
    private ResumeServiceImpl resumeService;'''

new_fields = '''    @Autowired
    private ResumeServiceImpl resumeService;

    @Autowired
    private WorkExperienceService workExperienceService;

    @Autowired
    private EducationService educationService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private ProjectExperienceService projectExperienceService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private LanguageService languageService;'''

content = content.replace(old_fields, new_fields)

with open(fp, "w", encoding="utf-8") as f:
    f.write(content)

print("OK - imports and fields updated")