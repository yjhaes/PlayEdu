from pathlib import Path
import subprocess

scratch = Path('.scratch/points-structure-cleanup')
names = {'PointBalanceChange': 'types', 'PointBalanceChangeResult': 'types', 'PointBalanceException': 'exception'}
extra = subprocess.check_output(['git', 'grep', '--cached', '-l', '-E', r'xyz\.playedu\.points\.service\.PointBalance(Change|ChangeResult|Exception)']).decode().splitlines()
for name in dict.fromkeys((scratch / 'affected-files.txt').read_text().splitlines() + extra):
    path = Path(name)
    if path.stem in names:
        continue
    key = path.as_posix()
    content = subprocess.check_output(['git', 'show', 'HEAD:' + key]).decode('utf-8')
    eol = '\r\n' if '\r\n' in content else '\n'
    for symbol, package in names.items():
        content = content.replace(f'xyz.playedu.points.service.{symbol}', f'xyz.playedu.points.{package}.{symbol}')
    if path.name == 'PointBalanceService.java':
        content = content.replace('/** The only', f'import xyz.playedu.points.types.PointBalanceChange;{eol}import xyz.playedu.points.types.PointBalanceChangeResult;{eol}{eol}/** The only')
    elif path.name == 'PointBalanceServiceIntegrationTest.java':
        content = content.replace('import xyz.playedu.points.domain.PointLedgerType;', f'import xyz.playedu.points.domain.PointLedgerType;{eol}import xyz.playedu.points.exception.PointBalanceException;{eol}import xyz.playedu.points.types.PointBalanceChange;{eol}import xyz.playedu.points.types.PointBalanceChangeResult;')
    lines = content.splitlines(keepends=True)
    indexes = [i for i, line in enumerate(lines) if line.startswith('import ') and not line.startswith('import static ')]
    for i, line in zip(indexes, sorted(lines[i] for i in indexes)):
        lines[i] = line
    sha = subprocess.check_output(['git', 'hash-object', '-w', '--stdin'], input=''.join(lines).encode('utf-8')).decode().strip()
    subprocess.run(['git', 'update-index', '--cacheinfo', '100644', sha, key], check=True)
