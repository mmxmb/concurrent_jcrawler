# Concurrent Java Crawler

A simple concurrent crawler that looks for a keyword on webpages of a specified website. Uses [jsoup library](https://jsoup.org/) for HTML parsing.

This is an ongoing personal project, more functionality will be added on top. 

## Usage

The crawler is fully functional at the moment. It can be used by creating an instance of `CrawlManager` (for single-threaded version)
or `CrawlManagerConcurrent` (for multithreaded version) and calling a `search` method of this instance. For more information refer
to the [documentation](https://cdn.rawgit.com/LawnboyMax/concurrent_jcrawler/39d26622/doc/lawnbway/crawler/CrawlManagerConcurrent.html).

## Documentation

[Project Overview](https://cdn.rawgit.com/LawnboyMax/concurrent_jcrawler/39d26622/doc/overview-summary.html)

## License

MIT License

Copyright (c) 2017 Max Lawnboy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


