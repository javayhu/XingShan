import sae
from ShanShan import wsgi

application = sae.create_wsgi_app(wsgi.application)
