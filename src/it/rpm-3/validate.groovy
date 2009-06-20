// $Id$
import org.codehaus.mojo.unix.rpm.RpmUtil
import org.codehaus.mojo.unix.rpm.RpmUtil.FileInfo
import org.codehaus.mojo.unix.rpm.RpmUtil.SpecFile

import java.util.List
import java.util.Iterator

boolean success = true

File rpm = new File((File) basedir, "target/rpm/project-rpm-3/RPMS/noarch/project-rpm-3-1.2-1.noarch.rpm")

success &= rpm.exists()

SpecFile spec = RpmUtil.getSpecFileFromRpm(rpm)

success &= spec.name.equals("project-rpm-3")
success &= spec.version.equals("1.2")
success &= spec.release == 1
success &= spec.license.equals("2009 my org")

List fileInfos = RpmUtil.queryPackageForFileInfo(rpm)


int fileCnt = fileInfos.size()
System.out.println("File Count: " + fileCnt);
System.out.println(fileInfos);
success &= fileCnt == 8

boolean linuxNameScript = false;
boolean nameScript = false;
boolean osNameScript = false;
boolean oldNameLink = false;

for (Iterator i = fileInfos.iterator(); i.hasNext();)
{
    FileInfo fileInfo = (FileInfo) i.next()
    success &= fileInfo.user.equals("myuser")
    success &= fileInfo.group.equals("mygroup")
    
    //check for executable mode
    if (fileInfo.path.startsWith("/usr/myusr/app/bin/"))
    {
        //oldname.sh is a link, so the filemode is different
        if (fileInfo.path.endsWith("/oldname.sh"))
        {
            oldNameLink = true;
            success &= fileInfo.mode.equals("lrwxr-xr-x")
        }
        else
        {
            success &= fileInfo.mode.equals("-rwxr-xr-x")
        
            if (fileInfo.path.endsWith("/name.sh"))
            {
                nameScript = true;
            }
            else if (fileInfo.path.endsWith("/name-someOS.sh"))
            {
                osNameScript = true;
            }
            else if (fileInfo.path.endsWith("/name-linux.sh"))
            {
                linuxNameScript = true;
            }
            
        }
    }
}

success &= nameScript;
success &= osNameScript;
success &= oldNameLink;
success &= (!linuxNameScript);

return success