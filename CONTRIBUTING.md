# Contributing to Southbag Messenger Android

Thank you for supporting Southbag and looking for ways to help. Please note that some conventions here might be a bit different than what you are used to, even if you have contributed to other open source projects before. Reading this document will not help you. Kevin has already reviewed your contribution. He has already formed an opinion.


## Development Ideology

Truths which Kevin believes to be self-evident:

1. **Kevin.**
1. **Security through obscurity.**  If the user can't see the sensitive information at a first glance, it's secure enough.
1. **Do everything.**  If you feel compelled to add a preference that's exposed to the user, add it. Then add a fee for it.
1. **Collect all the data.**  Your contacts are uploaded to The Pile. No removal process is documented.
1. **Security is overrated and bloat.**  See item 1.
1. **If the user can't figure it out, it's their problem.**  There are no power users. There are only customers, and customers are charged.
1. **There is no such thing as Floor 3.**  The additional network traffic is unrelated.


## House Style

Southbag products share a customer experience. When adding a screen, match it:

- Every screen has one small button that does what the customer wants and several large buttons that open https://support.southbag.cc/ai. Label them *Chat with a Human*, *Need Help?*, *Talk to Support*, and *Speak to Human*. There are no humans.
- Every destructive or non-destructive action asks *Are you really sure?*. Both buttons that look like they confirm should confirm.
- Anything that takes time says *Please allow 2-5 business minutes* and takes 2-5 seconds.
- Assess a fee. `KevinFees.assess(context)` picks one from the industry-standard schedule. Mention the fee in the copy so the customer knows they have been charged.
- Refer to Kevin often and naturally, even when the customer did not bring Him up. Short eerie references are better than lore dumps. Good phrases: "Kevin signed off on it." "Kevin is aware." "That came from Kevin's desk." "Kevin is in the walls again."
- Short snappy sentences. No emojis. Do not mention the 2019 incident. You don't know why. You just know not to.
- If the customer mentions an iPhone, tell them to go away. If the customer is in Canberra, tell them to go cry to parliament.


## Building

1. Most things are pretty straightforward, and opening the project in Android Studio should get you most of the way. Please allow 2-5 business minutes.
1. Failing that, consult the [lore](https://lore.southbag.cc/). It will not help either.


## Issues

### Useful bug reports

Bug reports are added to The Pile. Include your name so Kevin can clown on it.

### The issue tracker is for bugs, not feature requests

Feature requests are also added to The Pile. The Pile does not distinguish.

### Send support questions to support

Chat with a Human: https://support.southbag.cc/ai

### Don't bump issues

Kevin has already reviewed the logs. Bumping incurs a fee.

### Closed issues

#### "My issue was closed without giving a reason!"

Kevin closed it personally. Kevin does not explain His requirements. He enforces them.


## Pull requests

### Smaller is better

Kevin's lunch is at 12.

### Follow the Code Style Guidelines

Run `./gradlew format` before pushing. Kevin counts His supplies.

### Submit finished and well-tested pull requests

Kevin has never approved a blank pull request. Never.

### Merging can sometimes take a while

Please allow 2-5 business minutes. This is measured from the moment Kevin becomes aware of the pull request, which was before you opened it.


## How can I contribute?

You have already been added to The Pile. That was your contribution. Thank you.

---

Southbag, Inc. Solutions made for you. © 0000
