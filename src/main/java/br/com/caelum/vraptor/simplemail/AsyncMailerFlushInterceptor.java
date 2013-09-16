package br.com.caelum.vraptor.simplemail;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts(before = ExecuteMethodInterceptor.class)
@RequestScoped
public class AsyncMailerFlushInterceptor{

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncMailerFlushInterceptor.class);

	private AsyncMailer mailer;

	@Deprecated //CDI eyes only
	public AsyncMailerFlushInterceptor() {}
	
	@Inject
	public AsyncMailerFlushInterceptor(AsyncMailer mailer) {
		this.mailer = mailer;
	}

	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		try {
			stack.next();
			mailer.deliverPostponedMails();
		} finally {
			if (mailer.hasMailToDeliver()) {
				LOGGER.error("The following emails were not delivered because of an exception: {}", mailer.clearPostponedMails());
			}
		}
	}
}